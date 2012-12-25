package com.imseam.freamworktest.meeting;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IUserRequest;

@IMWindowScoped @Named("hostMeetingActionHandler")
public class HostActionHandler {
	
	private @Inject Instance<CDIMeeting> meeting; 
	private @Inject Instance<IUserRequest> request;
	
	public void startMeeting(){
		String buddies= request.get().getParameter("buddies");
	
		meeting.get().startMeetingWithBuddy(buddies.split(":::"));
	}
	
	public void distributeMessage(){
		meeting.get().send(request.get().getInput());
		
	}
	

}
