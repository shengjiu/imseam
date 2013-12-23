package com.imseam.cluster;

import java.util.List;


public interface IClusterCache{
	
	<T> void put(String key, T obj)throws ClusterLockException;
	
	<T> T putIfAbsent(String key, T obj) throws ClusterLockException;
	
	void remove(String... keys)throws ClusterLockException;
	
	<T> List<T> get(String... keys);
	
	<T> List<IFutureResult<T>> getInFuture(String... keys);
	
	<T> IFutureResult<T> getInFuture(String key);
	
	<T> T get(String key);
	
	<T> IClusterSet<T> getSet(String key);
	
	<T> IClusterMap<T> getMap(String key);
	
	<T> IClusterList<T> getList(String key);
	
	void lock(String...keys) throws ClusterLockException;
	
	void unlock(String...keys);
	
	IClusterTransaction startTransaction();
	
	void releaseToPool();
	
}
