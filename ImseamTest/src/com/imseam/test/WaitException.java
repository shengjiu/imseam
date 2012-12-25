package com.imseam.test;

public class WaitException extends Exception{

	private static final long serialVersionUID = 3898586896981692096L;
	
	public WaitException(String message){
		super(message);
	}
	
	public WaitException(String message, Throwable cause){
		super(message, cause);
	}

}
