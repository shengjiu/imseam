package com.imseam.cdi.chatlet.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.imseam.cdi.chatlet.ChatflowRequestProcessor;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IWindow;

@IMWindowScoped
public class Chatflow {
//	private static Log log = LogFactory.getLog(Chatflow.class);
	
	private @Inject Instance<IWindow> window;  
	
	private @Inject Instance<ChatflowRequestProcessor> chatflowRequestProcessor; 
	
	public void signal(String transition){
		chatflowRequestProcessor.get().getChatflow().signalTransition(transition);
	}
	
	public void begin(String chatflowDefinitionName, IAttributes request, String welcome) {
		chatflowRequestProcessor.get().getChatflow().begin(chatflowDefinitionName, request, window.get(), welcome);
	}


}
