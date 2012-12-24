package com.imseam.chatlet;


public interface IChatletFilterChain {
	
	void doFilter(IUserRequest request, IMessageSender sender);
	 
}
