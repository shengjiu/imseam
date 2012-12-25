package com.imseam.freamworktest;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import com.imseam.cdi.chatlet.components.CDIMeeting;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.context.ChatpageContext;


@IMWindowScoped @Named("testBean")
public class TestBean{
	
	private @Inject Instance<CDIMeeting> meeting;
	
	private @Inject Instance<PerformanceMeetingTestHelper> testHelper;
	
	private @Inject Instance<IUserRequest> request;
	
	private @Inject Instance<IMessageSender> sender;
	
	public TestBean(){

	}
	

	public String echo(){
//		String userInput = ChatpageContext.current().evaluateStringExp("#{request.input}");
//		sender.get().send(userInput);
		return "echo";
	}

	
	public String getCheckEchoOrMeeting(){
//		String input = request.get().getInput(); 
//		if("echo".equalsIgnoreCase(input)){
//			return "echo";
//		}
//		return "meeting";
		//return ChatpageContext.current().evaluateStringExp("#{request.input}");
		return "echo";
	}
	
	public String meeting(){
		String userInput = ChatpageContext.current().evaluateStringExp("#{request.input}");
		if(meeting.get().isInMeeting()){
			meeting.get().send(userInput);
		}else{
			String meetingBuddyUid = testHelper.get().getAvailableBuddyUid();
			
			if(meetingBuddyUid != null){
				
				meeting.get().startMeetingWithBuddy(meetingBuddyUid);
				
			}else{
				testHelper.get().addBuddyUid(request.get().getRequestFromChannel().getBuddy().getUid());
			}
		}
		
		
		return "meeting";
	}


}
