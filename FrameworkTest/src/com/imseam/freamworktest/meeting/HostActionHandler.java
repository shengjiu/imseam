package com.imseam.freamworktest.meeting;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.Id;
import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.chatlet.components.Chatflow;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.NoMeetingException;
import com.imseam.test.StringSerializerUtil;

@IMWindowScoped @Named("hostMeetingActionHandler")
public class HostActionHandler {
	
	private @Inject Instance<CDIMeeting> meeting; 
	private @Inject Instance<IUserRequest> request;
	private @Inject Instance<IMessageSender> sender;
	private @Inject Instance<Chatflow> chatflow;
	private @Inject Instance<IMeeting> meetingContext;
	private @Inject Instance<IWindow> windowContext;
	private String connectionId = (char)("NettyTest".length() + 1) + "NettyTest" + "server1";

	
	public String startMeeting(){
		String[] buddies= getBuddies();
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

	public void setValue2MeetingContext(){
		String encodedStr= request.get().getParameter("encodedStr");
		Object obj = StringSerializerUtil.of(encodedStr);
		
		
		meetingContext.get().setAttribute(encodedStr, obj);
		sender.get().send(request.get().getInput());
		
		try {
			meeting.get().fireMeetingEventToAllOtherWindows(new RetrieveMeetingContextEvent(Id.of(windowContext.get()), encodedStr), null);
		} catch (NoMeetingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startActiveWindow(){
		String[] buddies= getBuddies();
		meeting.get().addBuddyToMeeting(buddies);
	}
	
	private String[] getBuddies(){
		String[] buddies= request.get().getParameter("buddies").split(":::");
		for(int i = 0; i < buddies.length; i++){
			buddies[i] = constructBuddyUid(buddies[i]);
		}
		return buddies;
	}


	private String constructBuddyUid(String buddyId){
		char connectionUidLength = (char)(connectionId.length() + 1);
		return connectionUidLength + connectionId + buddyId;
	}
	

}
