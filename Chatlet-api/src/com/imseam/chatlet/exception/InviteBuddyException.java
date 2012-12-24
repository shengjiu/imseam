package com.imseam.chatlet.exception;

public class InviteBuddyException extends Exception {
	
	public enum Reason{
		userNameNotExisting,
		requestRefused,
		inviteBuddyNotSupported,
		unknown
	}
	
	private final Reason reason;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2517260737242687214L;

	public InviteBuddyException(Reason reason, String msg) {
		super(msg);
		this.reason = reason;
	}
	
	public InviteBuddyException(Reason reason, String msg, Throwable cause) {
		super(msg, cause);
		this.reason = reason;
	}
	
	public InviteBuddyException(Reason reason, Throwable cause) {
		super(cause);
		this.reason = reason;
	}
	
	public Reason getReason(){
		return reason;
	}
	
}
