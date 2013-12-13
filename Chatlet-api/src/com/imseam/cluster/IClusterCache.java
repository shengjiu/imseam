package com.imseam.cluster;

import java.util.List;


public interface IClusterCache{
	
	<T> void put(String key, T obj)throws LockException;
	
	<T> T putIfAbsent(String key, T obj) throws LockException;
	
	void remove(String... keys)throws LockException;
	
	<T> List<T> get(String... keys);
	
	<T> T get(String key);
	
	void lock(String...keys) throws LockException;
	
	void unlock(String...keys);
	
	IClusterTransaction startTransaction();
	
	void releaseToPool();
	
}
