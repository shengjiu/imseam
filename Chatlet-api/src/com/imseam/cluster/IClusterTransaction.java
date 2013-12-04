package com.imseam.cluster;

public interface IClusterTransaction {
	
	void commit();
	void rollback();
	
	void lock(String...keys) throws TimeoutForAcquireLockException;
	
	void optimisticLock(String...keys);
	
	void unlock(String...keys);

}
