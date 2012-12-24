package com.imseam.chatlet.exception;

public class MessageSenderNotAvailableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7228230385628113188L;

	/**
	 * 
	 */
	

	public MessageSenderNotAvailableException(String msg) {
		super(msg);
	}
	
	public MessageSenderNotAvailableException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public MessageSenderNotAvailableException(Throwable cause) {
		super(cause);
	}
}
