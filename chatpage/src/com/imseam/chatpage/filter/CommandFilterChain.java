package com.imseam.chatpage.filter;

import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;

public interface CommandFilterChain {
	
	void doCommandFilter(IChatPage chatpage, IUserRequest request, String input, IMessageSender responseSender); 

}
