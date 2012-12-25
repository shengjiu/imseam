package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import com.imseam.chatlet.IWindow;
import com.imseam.raptor.IChatletApplication;

public class OtherWindowLeftMeetingInvocation extends AbstractInMeetingInvocation{

	private static final long serialVersionUID = -1517721738695191724L;
	private String leftWindowUid = null;
	
	public OtherWindowLeftMeetingInvocation(String meetingUid, String sourceWindowUid, String leftWindowUid, Date timestamp){
		super( meetingUid, sourceWindowUid, timestamp);
		this.leftWindowUid = leftWindowUid;
	}

	@Override
	protected void executeTask(IChatletApplication application, IWindow window){
		application.getMeetingEventListener().onOtherWindowLeftMeeting(window, getSourceWindowUid(), leftWindowUid);
	}

	

	
}