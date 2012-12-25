package com.imseam.raptor.threading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExtendedThreadPoolExecutor extends ThreadPoolExecutor{

	public ExtendedThreadPoolExecutor(int corePoolSize,
			int maximumPoolSize,
			long keepAliveTime,
			TimeUnit unit,
			BlockingQueue<Runnable> workQueue){
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		assert(workQueue != null);
	}
	
	
	public <V, T> ExtendedFutureTask<V, T> submit(ComparableCallable<V, T> task) {
		if (task == null)
			throw new NullPointerException();
		ExtendedFutureTask<V, T> ftask = new ExtendedFutureTask<V, T>(task);
		execute(ftask);
		return ftask;
	}
}
