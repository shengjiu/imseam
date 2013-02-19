package com.imseam.cdi.chatlet.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.cdi.weld.WeldEngineHelper;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IChatPage;

@IMWindowScoped
public class ChatPageMapper{

	private @Inject Instance<IUserRequest> userRequest;
	private @Inject Instance<IMessageSender> responseSender;

	private IChatPage statefulChatPage = null; 
	
	public IChatPage getCurrentChatPage() {
		return statefulChatPage;
	}

	public void responseStatefulPage(String fullPathViewID) throws ChatPageRenderException {
		IChatPage chatPage = ChatPageManager.getInstance().getChatPage(fullPathViewID);
		chatPage.redenerBody(userRequest.get().getInput(), userRequest.get(),  responseSender.get());
		statefulChatPage = chatPage;
	}

	public void responseStatelessPage(String fullPathViewID) throws ChatPageRenderException {
		IChatPage chatPage = ChatPageManager.getInstance().getChatPage(fullPathViewID);
		if(chatPage != null)
			chatPage.redenerBody(userRequest.get().getInput(), userRequest.get(),  responseSender.get());
	}
	
	public void responseStatefulPage(IChatPage chatPage) throws ChatPageRenderException {
		chatPage.redenerBody(userRequest.get().getInput(), userRequest.get(),  responseSender.get());
		statefulChatPage = chatPage;
	}
	
	public void resetCurrentChatPage(){
		statefulChatPage = null;
	}

	public static ChatPageMapper instance() {
		ChatPageMapper instance = WeldEngineHelper.getInstance().getInstanceFromWeldEngine(ChatPageMapper.class);
		if(instance == null){
			System.out.println("ChatPageMapper is null!");
			instance = WeldEngineHelper.getInstance().getInstanceFromWeldEngine(ChatPageMapper.class);
		}
		return instance;
	}



}
