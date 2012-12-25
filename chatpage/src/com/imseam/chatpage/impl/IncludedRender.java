package com.imseam.chatpage.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IChatPage;
import com.imseam.chatpage.IResponseRender;
import com.imseam.common.util.StringUtil;

//only page body can be included

public class IncludedRender implements IResponseRender{
	
	
	private static Log log = LogFactory.getLog(IncludedRender.class);
	
		
	private String viewID;
	private String parentPath;
	private String fullPathViewID;
	
	public IncludedRender(String parentPath, String viewID){
		assert(!StringUtil.isNullOrEmptyAfterTrim(viewID));
		assert(!StringUtil.isNullOrEmptyAfterTrim(parentPath));
		if(viewID.indexOf(IChatPage.PathSeperator) >= 0){
			log.error("View ID cannot contain " + IChatPage.PathSeperator +":"+ viewID);
		}
		this.viewID = viewID;
		
		if(!parentPath.startsWith(IChatPage.PathSeperator)){
			log.error("Parent path must start with root: " + IChatPage.PathSeperator +":" + parentPath);
		}
		this.parentPath = parentPath;
		if(!parentPath.endsWith(IChatPage.PathSeperator)){
			parentPath = parentPath + IChatPage.PathSeperator;
		}
		fullPathViewID = parentPath + viewID;
	}


	public void render(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		
		IChatPage page = ChatPageManager.getInstance().getChatPage(fullPathViewID);
		page.redenerBody(input, request, responseSender);
		
	}


	public String getFullPathViewID() {
		return fullPathViewID;
	}


	public String getParentPath() {
		return parentPath;
	}


	public String getViewID() {
		return viewID;
	}

}
