package com.imseam.chatpage;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;

public interface IChatPage {
	static public String PathSeperator = "/";
	static public String RootPath = PathSeperator;

	void redenerBody(String input, IAttributes request, IMessageSender responseSender)  throws ChatPageRenderException;
	void redenerHelp(String input, IAttributes request, IMessageSender responseSender)  throws ChatPageRenderException;

	String parseAndProcessInput(IUserRequest request);
	
	String getParentPath();
	String getFullPathViewID();
	String getViewID();
	
	
	
}
