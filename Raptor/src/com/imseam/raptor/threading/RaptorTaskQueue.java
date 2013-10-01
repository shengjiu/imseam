package com.imseam.raptor.threading;

import java.io.Serializable;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class RaptorTaskQueue implements ComparableCallable<Object, Long>, Serializable{

	private static final long serialVersionUID = 2759888414669614302L;

	private static final Log log = LogFactory.getLog(RaptorTaskQueue.class);
		
	private static ConcurrentHashMap<String, RaptorTaskQueue> queueMap = new ConcurrentHashMap<String, RaptorTaskQueue>();
	
//	private volatile PrioritizedTask<Object, Long> nextCallable = null;
	
	private static Lock lock = new ReentrantLock();
	
	@SuppressWarnings("rawtypes")
	private volatile FutureTask futureTaskInThreadPool = null;

	private PriorityBlockingQueue<PrioritizedTask<Object, Long>> callableQueue = new PriorityBlockingQueue<PrioritizedTask<Object, Long>>(20, new InQueueTaskComparable());
	
	private volatile boolean isStopped = false;

	public static RaptorTaskQueue getInstance(String uid) {
		
//		System.out.println("RaptorTaskQueue uid: " + uid);
		if (uid == null) {
			throw new IllegalStateException("No active window context");
		}
		RaptorTaskQueue queue = queueMap.get(uid);
		if (queue == null) {
			queue = new RaptorTaskQueue();
			RaptorTaskQueue oldQueue = queueMap.putIfAbsent(uid, queue);
			if(oldQueue != null){
				queue = oldQueue;
			}
		}
		return queue;
	}
	
	public void stopQueue(){
		try{
			lock.lock();
			this.isStopped = true;
			if(futureTaskInThreadPool != null){
				futureTaskInThreadPool.cancel(false);
				futureTaskInThreadPool = null;
			}
			if(callableQueue.size() != 0){
				log.warn("The RaptorTaskQueue will be empty but it is not empty");
			}
			callableQueue.clear();
		}finally{
			lock.unlock();
		}
		
	}

	public void addTask(PrioritizedTask<Object, Long> message) {
		try{
			lock.lock();

			if(this.isStopped){
				log.warn("The RaptorTaskQueue was stopped: " + message);
				return;
			}

			assert(message.getPriority() != null);

			callableQueue.add(message);
			
			if(futureTaskInThreadPool == null){
				futureTaskInThreadPool = RaptorThreadPool.getInstance().addQueue(this);
			}
		}catch(Exception exp){
			log.error("Cannot add the conversationqueue to the conversation thread pool", exp);
		}finally{
			lock.unlock();
		}
	}

	public Object call() throws Exception {
		Object result = null;
		try{
			lock.lock();
			if(this.isStopped){
				log.warn("The RaptorTaskQueue was stopped, the call will return immediately");
				return null;
			}

			try{
				PrioritizedTask<Object, Long> nextCallable = callableQueue.poll();
				assert(nextCallable != null);
				result = nextCallable.call();
			}catch(Throwable exp){
				log.error("Task Excuting Error in Queue", exp);
				result = null;
			}
			
			try{
				if(callableQueue.peek() != null){
					futureTaskInThreadPool = RaptorThreadPool.getInstance().addQueue(this);
				}else{
					futureTaskInThreadPool = null;
				}
				return result;
			}catch(Throwable exp){
				log.error("Exception happened when polling the next task in ConversatoinMessageQueue:", exp);
				return result;
			}
		}
		finally{
			lock.unlock();
		}
	}



	public Long getPriority() {
		PrioritizedTask<Object, Long> nextCallable = callableQueue.peek();
		if(nextCallable == null){
			return new Long(-1);
		}
		return nextCallable.getPriority();
	}
	
	
	public int compareTo(Long otherPriority) {
		return otherPriority.compareTo(getPriority());
	}
	
	class InQueueTaskComparable implements Comparator<PrioritizedTask<Object, Long>>{

		public int compare(PrioritizedTask<Object, Long> task1, PrioritizedTask<Object, Long> task2) {
			
			return task1.getPriority().compareTo(task2.getPriority());
		}
		
	}
}
