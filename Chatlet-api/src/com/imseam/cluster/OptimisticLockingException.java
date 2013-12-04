package com.imseam.cluster;

public class OptimisticLockingException extends RuntimeException {

	private static final long serialVersionUID = 5278928995735672302L;

	public OptimisticLockingException(){
		super("The lock is changed by the other user");
	}

	public OptimisticLockingException(String msg){
		super(msg);
	}

}
