package com.imseam.cluster;

import java.util.List;


public interface IClusterSet<T> {
	
	void add(T...t) throws ClusterLockException;
	
	void remove(T...t) throws ClusterLockException;
	
	T[] getAll();
	
	boolean isExisting(T t);

	int size();
	
	IFutureResult<Boolean> isExistingInFuture(T t);
	
	IFutureResult<List<T>> getAllInFuture();
	
	IFutureResult<Integer> sizeInFuture();
	

}
