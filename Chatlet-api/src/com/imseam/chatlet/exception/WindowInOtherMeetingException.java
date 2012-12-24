package com.imseam.chatlet.exception;

public class WindowInOtherMeetingException extends Exception {

	private static final long serialVersionUID = -5538093638122680325L;
	private String windowUid;
	private String meetingUid;
	

	public WindowInOtherMeetingException(String msg,  String windowUid, String meetingUid) {
		super(msg);
		this.windowUid = windowUid;
		this.meetingUid = meetingUid;
	}
	
	public WindowInOtherMeetingException(String msg, Throwable cause, String windowUid, String meetingUid) {
		super(msg, cause);
		this.windowUid = windowUid;
		this.meetingUid = meetingUid;
	}
	
	public WindowInOtherMeetingException(Throwable cause, String windowUid, String meetingUid) {
		super(cause);
		this.windowUid = windowUid;
		this.meetingUid = meetingUid;
	}
	
	public String getWindowUid(){
		return windowUid;
	}
	
	public String getInvitingMeetingUid(){
		return meetingUid;
	}

}
