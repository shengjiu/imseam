package com.imseam.cluster;


public interface IClusterCache{
	
	<T> void put(String key, T obj);
	
	<T> T putIfAbsent(String key, T obj);
	
	void remove(String key);
	
	<T> T get(String key);
	
	void lock(String...keys) throws TimeoutForAcquireLockException;
	
	void optimisticLock(String...keys);
	
	void unlock(String...keys);
	
	IClusterTransaction startTransaction();
	
	void releaseToPoolWithRollback();
	
	void releaseToPoolWithCommit();
	
}
