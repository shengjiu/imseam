package com.imseam.chatlet;



public interface IChatlet extends IInitable{

	void serviceUserRequest(IUserRequest req, IMessageSender res);
		
}
