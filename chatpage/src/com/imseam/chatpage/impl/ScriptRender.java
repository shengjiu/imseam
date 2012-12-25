package com.imseam.chatpage.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IResponseRender;

public class ScriptRender implements IResponseRender {
	
	private static Log log = LogFactory.getLog(ScriptRender.class);
	
	
	public ScriptRender(String content){
		
	}
	@Override
	public void render(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		// TODO Auto-generated method stub
		log.warn("Script render haven't been implemented yet");
		
	}
}
