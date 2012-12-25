package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import com.imseam.chatlet.IWindow;
import com.imseam.raptor.IChatletApplication;

public class MeetingStoppedInvocation  extends AbstractInMeetingInvocation{

	private static final long serialVersionUID = 7583861742599572113L;
	
	public MeetingStoppedInvocation(String meetingUid, String sourceWindowUid, Date timeStamp){
		super(meetingUid, sourceWindowUid, timeStamp);
	}

	@Override
	public void executeTask(IChatletApplication application, IWindow window){
		application.getMeetingEventListener().onMeetingStopped(window, getSourceWindowUid());
	}
}