package com.imseam.freamworktest;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.imseam.cdi.chatlet.components.ChatPageMapper;
import com.imseam.cdi.chatlet.components.Chatflow;
import com.imseam.cdi.chatlet.ext.annotation.WindowStarted;
import com.imseam.cdi.chatlet.ext.annotation.WindowStopped;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.chatpage.ChatPageRenderException;

@IMWindowScoped
public class WindowEventListener {
	
	
	private @Inject Instance<Chatflow> chatflow; 
	private @Inject Instance<ChatPageMapper> chatPageMapper;
	
	public void onWindowInitialized(@Observes @WindowStarted WindowEvent event){ 
//		JbpmChatflow chatflow = chatflowRequestProcessor.get().getChatflow();
//		chatflow.begin("echo-chatflow", event, event.getWindow().getMessageSender(), "welcome");
//		if(meetingTestHelper.get().getFirstBuddyUid() == null){
//			meetingTestHelper.get().setFirstBuddyUid(event.getWindow().getDefaultChannel().getBuddy().getUid());
//		}else{
//			meeting.get().startMeetingWithBuddy(meetingTestHelper.get().getFirstBuddyUid());
//		}
		
		
//		chatflow.get().begin("performance-test-chatflow", event, "welcome");
		
		String userId = event.getWindow().getDefaultChannel().getBuddy().getUserId();
		System.out.println("Window: "+ event.getWindow() + ", " + event.getWindow().getUid() + ", userId:" + userId);
		
		if(userId.contains("operator")){
			try {
				chatPageMapper.get().responseStatefulPage("/com/imseam/freamworktest/welcome");
			} catch (ChatPageRenderException e) {
				e.printStackTrace();
			}
		}else{
			chatflow.get().begin("meeting-client", event, "welcome");
		}
		//System.out.println("Window: "+ event.getWindow() + ", " + event.getWindow().getUid() + ", JbpmChatflow: " + chatflow);
		
	}
	

	public void onWindowClose(@Observes @WindowStopped WindowEvent event){ 
		//System.out.println("Window closed:" + event.getWindow().getUid());
//		meetingTestHelper.get().remove(event.getWindow().getDefaultChannel().getBuddy().getUid());
	}

}
