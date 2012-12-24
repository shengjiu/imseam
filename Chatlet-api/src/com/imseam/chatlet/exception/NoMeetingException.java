package com.imseam.chatlet.exception;

public class NoMeetingException extends Exception {

	private static final long serialVersionUID = -6375208024377837668L;

	private String windowUid = null;

	public NoMeetingException(String msg, String windowUid) {
		super(msg);
		this.windowUid = windowUid;
	}
	
	public NoMeetingException(String msg, Throwable cause, String windowUid) {
		super(msg, cause);
		this.windowUid = windowUid;
	}
	
	public NoMeetingException(Throwable cause, String windowUid) {
		super(cause);
		this.windowUid = windowUid;
	}
	
	public String getWindowUid(){
		return windowUid;
	}
}
