package com.imseam.freamworktest.meeting;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.chatlet.components.Chatflow;
import com.imseam.cdi.chatlet.ext.annotation.meeting.JoinedMeeting;
import com.imseam.cdi.chatlet.spi.CDIExtendableMeetingEventListener;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;

@IMWindowScoped 
@Named("clientMeetingActionHandler")
@JoinedMeeting
public class ClientActionHandler extends CDIExtendableMeetingEventListener{
	
	private @Inject Instance<CDIMeeting> meeting; 
	private @Inject Instance<IUserRequest> request;
	private @Inject Instance<Chatflow> chatflow;
	
	private String meetingHost;
	
	public void messageRecieved(){
		meeting.get().send("recieved:::" +request.get().getInput(), meetingHost);
	}
	
	public void onJoinedMeeting(IWindow window, String sourceWindowUid){
		meetingHost = sourceWindowUid;
		chatflow.get().navigate("joinedMeeting");
	}
	
	

}
