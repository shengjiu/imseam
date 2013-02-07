package com.imseam.raptor.cluster.invocation.exception;

public class MeetingNotExistingException extends Exception {


	private static final long serialVersionUID = -4426619545846702016L;
	
	private String meetingUid = null;

	public MeetingNotExistingException(String meetingUid) {
		super(meetingUid + " not existing");
		this.meetingUid = meetingUid;
	}

	public String getMeetingUid() {
		return meetingUid;
	}
	
}