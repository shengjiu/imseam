package com.imseam.chatlet;


public interface IMessageSender {
	
	void send(IChatletMessage ... responseMessages);
	
	void send(String message);
	
	void flush();

}
