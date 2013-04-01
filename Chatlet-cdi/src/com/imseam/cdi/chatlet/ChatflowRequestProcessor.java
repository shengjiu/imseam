package com.imseam.cdi.chatlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.cdi.weld.WeldEngineHelper;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatpage.pageflow.ChatPageNode;
import com.imseam.chatpage.pageflow.JbpmChatflow;
import com.imseam.common.util.StringUtil;

@IMWindowScoped
public class ChatflowRequestProcessor{
	private static final Log log = LogFactory.getLog(ChatflowRequestProcessor.class);
	private JbpmChatflow jbpmChatflow = new JbpmChatflow(); 

	public ChatflowRequestProcessor(){
		
	}
	
	public boolean processChatRequest(IUserRequest chatRequest, IMessageSender responseSender) {
		String input = chatRequest.getInput();
		if (StringUtil.isNullOrEmpty(input)) {
			log.warn("Null or empty chat request input received");
			return false;
		}
		
		return jbpmChatflow.processChatInput(input, chatRequest, responseSender);
		
//		if (!jbpmChatflow.get().processChatInput(input)) {
//			Events.instance().raiseEvent(BuildInEventEnum.REQUEST_RECEIVED, chatRequest);
//		}
	}
	
	public boolean isInProcess(){
		return jbpmChatflow.isInProcess();
	}
	
	public ChatPageNode getCurrentChatPage(){
		return jbpmChatflow.getChatPageNode(); 
	}
	
	public boolean signalTransition(String transitionName){
		if(!this.isInProcess()){
			return false;
		}
		
		return jbpmChatflow.signalTransition(transitionName);
	}
	
	public void begin(String chatflowDefinitionName, IAttributes request, IWindow window, String welcome){
		jbpmChatflow.begin(chatflowDefinitionName, request, window, welcome);
	}

	public void end(){
		jbpmChatflow.end();
		jbpmChatflow = new JbpmChatflow();
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
		if(jbpmChatflow.getChatPageNode() == null) return null;
		return jbpmChatflow.getChatPageNode().getName();
	}
	

}
