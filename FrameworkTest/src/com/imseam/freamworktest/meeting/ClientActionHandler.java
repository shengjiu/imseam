package com.imseam.freamworktest.meeting;

import javax.enterprise.event.Observes;
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
	
	private String meetingHostWindow;
	
	public void messageRecieved(){
		System.out.println("client messagereceived: " +"recieved:::" +request.get().getInput() +", meetingHostWindow"+meetingHostWindow);
		meeting.get().send(request.get().getInput(), meetingHostWindow);
	}
	
	public void onJoinedMeeting(@Observes @JoinedMeeting MeetingEvent meetingEvent){
		System.out.println(String.format("client action handler get joinmeeting event, window: %s, source window:%s", meetingEvent.getWindow().getUid(), meetingEvent.getSourceWindowId()));
		meetingHostWindow =  meetingEvent.getSourceWindowId();
		System.out.println("meeting started:::" + meetingEvent.getWindow().getDefaultChannel().getBuddy().getUserId()+ ", meetingHostBuddy: "+ meetingHostWindow);
		meeting.get().send("meeting started:::" + meetingEvent.getWindow().getDefaultChannel().getBuddy().getUserId(), meetingHostWindow);
		
	}
	
	

}
