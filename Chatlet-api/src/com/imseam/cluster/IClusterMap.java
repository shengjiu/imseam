package com.imseam.cluster;

import java.util.Map;

public interface IClusterMap <T>{
	
	void put(String key, T t) throws ClusterLockException;
	
	void putIfAbsent(String key, T t) throws ClusterLockException;
	
	void remove(String...keys) throws ClusterLockException;
	
	T get(String key);
	
	Map<String, T> getAll();
	
	int size();
	
	IFutureResult<T>  getInFuture(String key);
	
	IFutureResult<Map<String, T>> getAllInFuture();
	
	IFutureResult<Integer> sizeInFuture();


}
