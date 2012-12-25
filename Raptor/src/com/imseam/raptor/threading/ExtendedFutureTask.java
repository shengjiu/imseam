package com.imseam.raptor.threading;

import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExtendedFutureTask<V, T> extends FutureTask<V> implements Comparable<ExtendedFutureTask<V, T>> {
	private static Log log = LogFactory.getLog(ExtendedFutureTask.class);
	
	private ComparableCallable<V, T> callable = null;
	
	
	public ExtendedFutureTask(ComparableCallable<V, T> callable) {
		super(callable);
		this.callable = callable;
	}

	public int compareTo(ExtendedFutureTask<V, T> otherTask) {
		T otherPriority = null;
		int result = 0;
		try{
			otherPriority = otherTask.getComparableCallable().getPriority();
			result = this.callable.compareTo(otherPriority);
		}catch(Exception exp){
			log.warn("Task comparing error:", exp);
		}
		return result;
	}
	
	public ComparableCallable<V, T> getComparableCallable(){
		return callable;
	}

}
