package com.imseam.raptor.cluster;

public class NoMeetingListenerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1935161134523484172L;
	
	public NoMeetingListenerException(String msg) {
		super(msg);
	}
	
	public NoMeetingListenerException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public NoMeetingListenerException(Throwable cause) {
		super(cause);
	}
}
