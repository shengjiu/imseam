package com.imseam.chatlet.exception;

public class SessionExpiredException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3275211076374504870L;

	public SessionExpiredException(String msg) {
		super(msg);
	}
	
	public SessionExpiredException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public SessionExpiredException(Throwable cause) {
		super(cause);
	}
}
