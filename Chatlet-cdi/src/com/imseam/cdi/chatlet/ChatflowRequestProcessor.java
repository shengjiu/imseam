package com.imseam.cdi.chatlet;

import java.lang.annotation.Annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.cdi.weld.WeldEngineHelper;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.pageflow.ChatPageNode;
import com.imseam.chatpage.pageflow.JbpmChatflow;
import com.imseam.common.util.StringUtil;

@IMWindowScoped
public class ChatflowRequestProcessor {
	private static final Log log = LogFactory.getLog(ChatflowRequestProcessor.class);
	private JbpmChatflow jbpmChatflow = new JbpmChatflow(); 

	public ChatflowRequestProcessor(){
		
	}
	
	public void processChatRequest(IUserRequest chatRequest, IMessageSender responseSender) {
		String input = getStringInput(chatRequest);
		if (StringUtil.isNullOrEmpty(input)) {
			log.warn("Null or empty chat request input received");
			return;
		}
		
		jbpmChatflow.processChatInput(input, chatRequest, responseSender);
		
//		if (!jbpmChatflow.get().processChatInput(input)) {
//			Events.instance().raiseEvent(BuildInEventEnum.REQUEST_RECEIVED, chatRequest);
//		}
	}
	
	public ChatPageNode getCurrentChatPage(){
		return jbpmChatflow.getChatPageNode(); 
	}
	
//	private boolean processSystemEventForChatflow(ChatflowEventEnum event){
//		return jbpmChatflow.processSystemEvent(event.toString());
//	}

	public boolean signalTransition(Annotation annotation){
		
		return jbpmChatflow.signalTransition(annotation.toString());
	}

	
//	public boolean processKickoutFromMeetingEvent(){
//		return processSystemEventForChatflow(ChatflowEventEnum.KickedoutFromMeeting);
//	}
//	
//	public boolean processMeetingStoppedEvent(){
//		return processSystemEventForChatflow(ChatflowEventEnum.MeetingStopped);
//	}
//	
//	public boolean processJoinedMeetingEvent(){
//		return processSystemEventForChatflow(ChatflowEventEnum.JoinedMeeting);
//	}
//	
//	public boolean processUserJoinWindowEvent(){
//		return processSystemEventForChatflow(ChatflowEventEnum.UserJoinWindow);
//	}
//	
//	public boolean processUserLeaveWindowEvent(){
//		return processSystemEventForChatflow(ChatflowEventEnum.UserLeaveWindow);
//	}
//	
//	public boolean processSessionStoppedEvent(){
//		return processSystemEventForChatflow(ChatflowEventEnum.SessionStopped);
//	}
	
	private String getStringInput(IUserRequest chatRequest) {
		if (chatRequest.getRequestContent().getMessageContent() instanceof String) {
			return (String)chatRequest.getRequestContent().getMessageContent();
		} else {
			log.warn("Chat Request type is not supported");
			return null;
		}
	}

	
	
	public static ChatflowRequestProcessor instance() {
		ChatflowRequestProcessor instance = WeldEngineHelper.getInstance().getInstanceFromWeldEngine(ChatflowRequestProcessor.class);
		if(instance == null){
			System.out.println("ChatflowRequestProcessor is null!");
			instance = WeldEngineHelper.getInstance().getInstanceFromWeldEngine(ChatflowRequestProcessor.class);
		}
		return instance;
	}
	
	public String getChatflowName(){
		return jbpmChatflow.getChatflowDefinitionName();
	}
	
	public String getState(){
		return jbpmChatflow.getChatPageNode().getName();
	}
	
	
	public JbpmChatflow getChatflow(){
		return jbpmChatflow;
	}
}
