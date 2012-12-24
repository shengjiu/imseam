package com.imseam.chatlet.exception;

public class ChannelExpiredException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3275211076374504870L;

	public ChannelExpiredException(String msg) {
		super(msg);
	}
	
	public ChannelExpiredException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ChannelExpiredException(Throwable cause) {
		super(cause);
	}
}
