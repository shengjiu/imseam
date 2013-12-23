package com.imseam.cluster;

public interface IClusterTransaction {
	
	void commit() throws ClusterLockException;
	
	void rollback();
	
}
