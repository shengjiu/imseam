package com.imseam.chatpage.impl;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageRenderException;

public class DefaultChatPage extends AbstractChatPage {
	
	//private static Log log = LogFactory.getLog(DefaultChatPage.class);
	
	
	public  DefaultChatPage(){
	}

	public void redenerBody(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		if(getBody() != null)  getBody().render(input, request, responseSender);
	}

	public void redenerHelp(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		if(getHelp() != null)  getHelp().render(input, request, responseSender);
	}

//	private ChatPageData createChatPageData() {
//		return new ChatPageData(this.getFullPathViewID(), DefaultChatPage.class.toString());
//	}

}
