package com.imseam.chatpage.filter;

import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;

public interface CommandFilter {
	
	void doCommandFilter(IChatPage chatpage, IUserRequest request, String input, CommandFilterChain chain, IMessageSender responseSender); 
	
	boolean parseInput(String input, IUserRequest request);

}

