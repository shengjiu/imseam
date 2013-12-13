package com.imseam.cluster;

public interface IClusterTransaction {
	
	void commit() throws LockException;
	
	void rollback();
	
}
