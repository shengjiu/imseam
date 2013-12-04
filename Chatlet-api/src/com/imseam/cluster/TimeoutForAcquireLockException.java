package com.imseam.cluster;

public class TimeoutForAcquireLockException extends Exception {


	private static final long serialVersionUID = -4340336768467176788L;
	
	public TimeoutForAcquireLockException(){
		super();
	}

	public TimeoutForAcquireLockException(String msg){
		super(msg);
	}

}
