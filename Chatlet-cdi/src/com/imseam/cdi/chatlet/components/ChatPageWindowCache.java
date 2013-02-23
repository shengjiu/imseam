package com.imseam.cdi.chatlet.components;

import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatpage.IChatPage;

//chatpage self controlled requestprocess

@IMWindowScoped
public class ChatPageWindowCache {

	private IChatPage statefulChatPage = null;

	public void setStatefulChatPage(IChatPage statefulChatPage) {
		this.statefulChatPage = statefulChatPage;
	}

	public IChatPage getCurrentChatPage() {
		return statefulChatPage;
	}


}
