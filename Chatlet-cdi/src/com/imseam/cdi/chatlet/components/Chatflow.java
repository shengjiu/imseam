package com.imseam.cdi.chatlet.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.imseam.cdi.chatlet.ChatflowRequestProcessor;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IWindow;
import com.imseam.common.util.StringUtil;

@IMWindowScoped
public class Chatflow {
//	private static Log log = LogFactory.getLog(Chatflow.class);
	
	private @Inject Instance<IWindow> window;  
	
	private @Inject Instance<ChatflowRequestProcessor> chatflowRequestProcessor; 
	
	public void navigate(IAttributes request, IMessageSender responseSender, String outcome) {
		assert (!StringUtil.isNullOrEmptyAfterTrim(outcome));
		chatflowRequestProcessor.get().getChatflow().navigate(request, responseSender, outcome);
	}
	
	public void begin(String chatflowDefinitionName, IAttributes request, String welcome) {
		chatflowRequestProcessor.get().getChatflow().begin(chatflowDefinitionName, request, window.get(), welcome);
	}


}
