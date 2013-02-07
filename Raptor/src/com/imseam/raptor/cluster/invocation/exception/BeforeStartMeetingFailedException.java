package com.imseam.raptor.cluster.invocation.exception;

public class BeforeStartMeetingFailedException extends Exception {


	private static final long serialVersionUID = -4426619545846702016L;
	
	private String meetingUid = null;

	public BeforeStartMeetingFailedException(String meetingUid) {
		super(meetingUid + " before meeting failed");
		this.meetingUid = meetingUid;
	}

	public String getMeetingUid() {
		return meetingUid;
	}
	
}