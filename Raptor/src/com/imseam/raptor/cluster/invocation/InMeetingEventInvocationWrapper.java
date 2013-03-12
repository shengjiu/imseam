package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.raptor.IChatletApplication;

public class InMeetingEventInvocationWrapper extends AbstractInMeetingInvocation{

	private static final long serialVersionUID = -8858611989428898564L;
	private IEvent event = null;
	
	public InMeetingEventInvocationWrapper(String meetingUid, IEvent event, Date timeStamp){
		super(meetingUid, event.getSource() == null ? null : event.getSource().toString(), timeStamp);
		this.event = event;
	}

	@Override
	protected void executeTask(IChatletApplication application, IWindow window) {
		try{
			application.getMeetingEventListener().onEventReceivedInMeeting(window, event);
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}

	@Override
	public String toString() {
		
		return "InMeetingEventInvocationWrapper, event:" + event.toString();
	}
	
	
	
}
