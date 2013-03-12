package com.imseam.freamworktest.meeting;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.chatlet.components.Chatflow;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;

@IMWindowScoped @Named("hostMeetingActionHandler")
public class HostActionHandler {
	
	private @Inject Instance<CDIMeeting> meeting; 
	private @Inject Instance<IUserRequest> request;
	private @Inject Instance<IMessageSender> sender;
	private @Inject Instance<Chatflow> chatflow;
	private String connectionId = (char)("NettyTest".length() + 1) + "NettyTest" + "server1";
	
	public String startMeeting(){
		String[] buddies= request.get().getParameter("buddies").split(":::");
		for(int i = 0; i < buddies.length; i++){
			buddies[i] = constructBuddyUid(buddies[i]);
		}
		meeting.get().startMeetingWithBuddy(buddies);
		
		request.get().setAttribute("host", Boolean.TRUE);
		
//		sender.get().send("meeting started:::" + request.get().getInput());
		
		chatflow.get().begin("meeting-operator", request.get(), "startmeeting");
		return "startmeeting";
	}
	
	public void stopMeeting(){
		meeting.get().stopMeeting();
		return;
	}
	
	public void distributeMessage(){
		meeting.get().send(request.get().getInput());
	}


	private String constructBuddyUid(String buddyId){
		char connectionUidLength = (char)(connectionId.length() + 1);
		return connectionUidLength + connectionId + buddyId;
	}
	

}
