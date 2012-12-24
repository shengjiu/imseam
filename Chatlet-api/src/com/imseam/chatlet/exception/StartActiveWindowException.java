package com.imseam.chatlet.exception;

public class StartActiveWindowException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5538093638122680325L;
	
	private String buddyUid;

	public StartActiveWindowException(String msg, String buddyUid) {
		super(msg);
		this.buddyUid = buddyUid;
	}
	
	public StartActiveWindowException(String msg, Throwable cause, String buddyUid) {
		super(msg, cause);
		this.buddyUid = buddyUid;
	}
	
	public StartActiveWindowException(Throwable cause, String buddyUid) {
		super(cause);
		this.buddyUid = buddyUid;
	}

	public String getBuddyUid() {
		return buddyUid;
	}
}
