package com.imseam.cluster;

import java.util.List;

public interface IClusterList <T> {
	
	void add(T...ts) throws ClusterLockException;
	
	void remove(int... indexes) throws ClusterLockException;
	
	T get(int i);
	
	List<T> getAll();
	
	int size();
	
	IFutureResult<T>  getInFuture(int i);
	
	IFutureResult<List<T>> getAllInFuture();
	
	IFutureResult<Integer> sizeInFuture();


}
