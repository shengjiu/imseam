package com.imseam.chatpage.impl;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IChatPage;

public class ChatPageWrapper implements IChatPage{
	
	//private static Log log = LogFactory.getLog(ChatPageWrapper.class);
	
	private AbstractChatPage chatPage = null;
	
	
	public  ChatPageWrapper(){
	}

	@Override
	public void redenerBody(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		chatPage.redenerBody(input, request, responseSender);
	}

	@Override
	public void redenerHelp(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		chatPage.redenerHelp(input, request, responseSender);
	}

	@Override
	public String parseAndProcessInput(String input, IUserRequest request) {
		return chatPage.parseAndProcessInput(input, request);
	}

	@Override
	public String getParentPath() {
		return chatPage.getParentPath();
	}

	@Override
	public String getFullPathViewID() {
		return chatPage.getFullPathViewID();
	}

	@Override
	public String getViewID() {
		return chatPage.getViewID();
	}


}
