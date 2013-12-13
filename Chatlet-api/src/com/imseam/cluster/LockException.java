package com.imseam.cluster;

public class LockException extends Exception {


	private static final long serialVersionUID = -4340336768467176788L;
	
	public LockException(){
		super();
	}

	public LockException(String msg){
		super(msg);
	}

}
