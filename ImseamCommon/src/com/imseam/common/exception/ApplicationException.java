package com.imseam.common.exception;


public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4347683702775965230L;
	
	public ApplicationException(String msg) {
		super(msg);
	}
	
	public ApplicationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ApplicationException(Throwable cause) {
		super(cause);
	}


}
