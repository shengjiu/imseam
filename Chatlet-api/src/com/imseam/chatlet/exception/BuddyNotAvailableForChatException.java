package com.imseam.chatlet.exception;

public class BuddyNotAvailableForChatException extends Exception {
	
	enum Reason{
		busy,
		notOnline,
		notActive
	}
	
	private final Reason reason;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2517260737242687214L;

	public BuddyNotAvailableForChatException(Reason reason, String msg) {
		super(msg);
		this.reason = reason;
	}
	
	public BuddyNotAvailableForChatException(Reason reason, String msg, Throwable cause) {
		super(msg, cause);
		this.reason = reason;
	}
	
	public BuddyNotAvailableForChatException(Reason reason, Throwable cause) {
		super(cause);
		this.reason = reason;
	}
	
	public Reason getReason(){
		return reason;
	}
	
}
