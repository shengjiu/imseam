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
	
	private volatile PrioritizedTask<Object, Long> runningTask = null;
	
	private static Lock lock = new ReentrantLock();
	
	@SuppressWarnings("rawtypes")
	private volatile FutureTask futureTask = null;

	private PriorityBlockingQueue<PrioritizedTask<Object, Long>> taskQueue = new PriorityBlockingQueue<PrioritizedTask<Object, Long>>(20, new InQueueTaskComparable());
	
	private volatile boolean isStopped = false;

	public void addTask(PrioritizedTask<Object, Long> message) {
		
		if(this.isStopped){
			log.warn("The conversation was stopped: " + message);
			return;
		}

		assert(message.getPriority() != null);
		try{
			taskQueue.add(message);
		}
		catch(ClassCastException cce){
			log.error("Cannot add the task in conversationqueue" + message, cce);
			return;
		}
		try{
			lock.lock();
			
			if(futureTask == null){
				futureTask = RaptorThreadPool.getInstance().addQueue(this);
			}
		}catch(Exception exp){
			log.error("Cannot add the conversationqueue to the conversation thread pool", exp);
		}finally{
			lock.unlock();
		}
	}
	

	
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
	
	public void queueStopped(){
		this.isStopped = true;
		lock.lock();
		try{
			if(futureTask != null){
				futureTask.cancel(false);
				futureTask = null;
			}
			if(taskQueue.size() != 0){
				log.warn("The conversation queue will be empty but it is not empty");
			}
			taskQueue.clear();
			runningTask = null;
		}finally{
			lock.unlock();
		}
		
	}


	public Object call() throws Exception {
		Object result = null;
		try{
			if(runningTask == null){
				runningTask = taskQueue.poll();
			}
			if(runningTask != null){
				result = runningTask.call();
			}else{
				return result;
			}
		}catch(Exception exp){
			log.error("Task Excuting Error in Queue", exp);
		}
		
		lock.lock();

		try{
			runningTask = taskQueue.poll();
			if(runningTask != null){
			
				futureTask = RaptorThreadPool.getInstance().addQueue(this);
			}else{
				futureTask = null;
			}
			return result;
		}catch(Exception exp){
			log.error("Error happened when polling the next task in ConversatoinMessageQueue:", exp);
			throw exp;
		}finally{
			lock.unlock();
		}
	}



	public Long getPriority() {
		if(runningTask == null){
			runningTask = taskQueue.poll();
		}
		if(runningTask == null){
			return new Long(-1);
		}
		return runningTask.getPriority();
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
