package com.imseam.cdi.chatlet.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.imseam.cdi.chatlet.ChatflowRequestProcessor;
import com.imseam.cdi.chatlet.ext.annotation.RequestObject;
import com.imseam.cdi.context.IMRequestScoped;
import com.imseam.cdi.weld.WeldEngineHelper;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IChatPage;

//chatpage self controlled requestprocess

@IMRequestScoped
public class ChatPageMapper {

	private @Inject
	Instance<IUserRequest> userRequest;
	private @Inject @RequestObject
	Instance<IAttributes> requestObject;

	private @Inject
	Instance<IMessageSender> responseSender;

	private @Inject Instance<ChatPageWindowCache> pageCache;
	
	public IChatPage getCurrentChatPage() {
		return pageCache.get().getCurrentChatPage();
	}

	public boolean isInProcess() {
		return pageCache.get().getCurrentChatPage() != null;
	}

	public boolean processChatRequest(IUserRequest chatRequest, IMessageSender responseSender) {
		String outcome = pageCache.get().getCurrentChatPage().parseAndProcessInput(chatRequest);

		if(ChatflowRequestProcessor.instance().isInProcess()){
			return true;
		}
		if (outcome != null) {
			IChatPage nextChatPage = ChatPageManager.getInstance().getChatPage(pageCache.get().getCurrentChatPage().getParentPath(), outcome);
			if (nextChatPage == null) {
				nextChatPage = ChatPageManager.getInstance().getChatPage(outcome);
			}

			if (nextChatPage != null) {
				try {
					responseStatefulPage(nextChatPage);
					return true;
				} catch (ChatPageRenderException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public void responseStatefulPage(String fullPathViewID) throws ChatPageRenderException {
		IChatPage chatPage = ChatPageManager.getInstance().getChatPage(fullPathViewID);
		if(chatPage == null) return;
		String input = null;
		if(userRequest.get() != null){
			input = userRequest.get().getInput();
		}
		IAttributes a = requestObject.get();
		chatPage.redenerBody(input, requestObject.get(), responseSender.get());
		pageCache.get().setStatefulChatPage(chatPage);
	}

	public void responseStatelessPage(String fullPathViewID) throws ChatPageRenderException {
		IChatPage chatPage = ChatPageManager.getInstance().getChatPage(fullPathViewID);
		if(chatPage == null) return;
		String input = null;
		if(userRequest.get() != null){
			input = userRequest.get().getInput();
		}
		chatPage.redenerBody(input, requestObject.get(), responseSender.get());
	}

	public void responseStatefulPage(IChatPage chatPage) throws ChatPageRenderException {
		String input = null;
		if(userRequest.get() != null){
			input = userRequest.get().getInput();
		}
		chatPage.redenerBody(input, requestObject.get(), responseSender.get());
		pageCache.get().setStatefulChatPage(chatPage);
	}

	public void resetStatefulChatPage() {
		pageCache.get().setStatefulChatPage(null);
	}

	public static ChatPageMapper instance() {
		ChatPageMapper instance = WeldEngineHelper.getInstance().getInstanceFromWeldEngine(ChatPageMapper.class);
		if (instance == null) {
			System.out.println("ChatPageMapper is null!");
			instance = WeldEngineHelper.getInstance().getInstanceFromWeldEngine(ChatPageMapper.class);
		}
		return instance;
	}

}
