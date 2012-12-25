package com.imseam.chatpage.impl;

import java.util.List;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IResponseRender;

public class DefaultResponseRender implements IResponseRender{

	List<IResponseRender> renderList = null;
	
	public DefaultResponseRender(List<IResponseRender> renderList){
		this.renderList = renderList;
	}

	public void render(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		if(renderList != null){
			for(IResponseRender render : renderList){
				render.render(input, request, responseSender);
			}
		}	
	}

}
