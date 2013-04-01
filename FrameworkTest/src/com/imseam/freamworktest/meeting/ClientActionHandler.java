package com.imseam.freamworktest.meeting;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.chatlet.event.MeetingEvent;
import com.imseam.cdi.chatlet.ext.annotation.meeting.JoinedMeeting;
import com.imseam.cdi.chatlet.ext.annotation.meeting.ReceivedMeetingEvent;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.test.StringSerializerUtil;

@IMWindowScoped 
@Named("clientMeetingActionHandler")
public class ClientActionHandler{
	
	private @Inject Instance<CDIMeeting> meeting; 
	private @Inject Instance<IUserRequest> request;
	private @Inject Instance<IMeeting> meetingContext;
	private @Inject Instance<IWindow> windowContext;
	
	private String meetingHostWindow;
	
	public void messageRecieved(){
//		System.out.println("ClientActionHandler messagereceived: " +"recieved:::" +request.get().getInput() +", meetingHostWindow:"+meetingHostWindow);
		meeting.get().send(request.get().getInput(), meetingHostWindow);
	}
	
	public void onJoinedMeeting(@Observes @JoinedMeeting MeetingEvent meetingEvent){
//		System.out.println(String.format("client action handler get joinmeeting event, window: %s, source window:%s", meetingEvent.getWindow().getUid(), meetingEvent.getSourceWindowId()));
		meetingHostWindow =  meetingEvent.getSourceWindowId();
//		System.out.println("meeting started:::" + meetingEvent.getWindow().getDefaultChannel().getBuddy().getUserId()+ ", meetingHostBuddy: "+ meetingHostWindow);
		meeting.get().send("meeting started:::" + meetingEvent.getWindow().getDefaultChannel().getBuddy().getUserId(), meetingHostWindow);
		
		
	}
	
	public void onFunctionBasedEventReceived(@Observes @ReceivedMeetingEvent RetrieveMeetingContextEvent event){
		String key = event.getEncodeStr();
		Object obj = meetingContext.get().getAttribute(key);
		
		String encodedStr = StringSerializerUtil.from(obj);		
		
		meeting.get().send(encodedStr + ":::" + windowContext.get().getDefaultChannel().getBuddy().getUserId(), meetingHostWindow);
		
	}

	

	

}
