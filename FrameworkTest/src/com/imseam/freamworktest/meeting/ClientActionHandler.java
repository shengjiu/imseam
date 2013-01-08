package com.imseam.freamworktest.meeting;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.chatlet.event.MeetingEvent;
import com.imseam.cdi.chatlet.ext.annotation.meeting.JoinedMeeting;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IUserRequest;

@IMWindowScoped 
@Named("clientMeetingActionHandler")
public class ClientActionHandler{
	
	private @Inject Instance<CDIMeeting> meeting; 
	private @Inject Instance<IUserRequest> request;
	
	private String meetingHost;
	
	public void messageRecieved(){
		meeting.get().send("recieved:::" +request.get().getInput(), meetingHost);
	}
	
	public void onJoinedMeeting(@JoinedMeeting MeetingEvent meetingEvent){
		System.out.println(String.format("client action handler get joinmeeting event: ", meetingEvent.getWindow().getUid(), meetingEvent.getSourceWindowId()));
		
	}
	
	

}
