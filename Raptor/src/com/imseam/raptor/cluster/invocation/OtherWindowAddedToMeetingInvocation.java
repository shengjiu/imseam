package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import com.imseam.chatlet.IWindow;
import com.imseam.raptor.IChatletApplication;

public class OtherWindowAddedToMeetingInvocation extends AbstractInMeetingInvocation{
	
	private static final long serialVersionUID = -8026192842279538037L;
	private String  addedWindowId = null;
	
	public OtherWindowAddedToMeetingInvocation(String meetingUid, String sourceWindowUid, String addedWindowId, Date timeStamp){
		super( meetingUid, sourceWindowUid, timeStamp);
		this.addedWindowId = addedWindowId;
	}

	@Override
	public void executeTask(IChatletApplication application, IWindow window) {			
		application.getMeetingEventListener().onOtherWindowJoinedMeeting(window, getSourceWindowUid(), addedWindowId);
	}
}