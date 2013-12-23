package com.imseam.cluster;

public class ClusterLockException extends Exception {


	private static final long serialVersionUID = -4340336768467176788L;
	
	public ClusterLockException(){
		super();
	}

	public ClusterLockException(String msg){
		super(msg);
	}

}
