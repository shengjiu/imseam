package com.imseam.chatpage;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;

public interface IResponseRender {
	
//	void render(OutputStream out) throws ChatPageRenderException;
//	String render() throws ChatPageRenderException;
	void render(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException;

}
