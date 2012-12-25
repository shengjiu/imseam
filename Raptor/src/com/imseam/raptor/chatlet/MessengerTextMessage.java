package com.imseam.raptor.chatlet;

import com.imseam.chatlet.IChatletMessage;

public class MessengerTextMessage implements IChatletMessage {
	
	private Object trigger;
	private String textMessage;

	public MessengerTextMessage(String textMessage){
		this.textMessage = textMessage;
	}

	public MessengerTextMessage(Object trigger, String textMessage){
		this.trigger = trigger;
		this.textMessage = textMessage;
	}

	@Override
	public Object getMessageSource() {
		return trigger;
	}

	@Override
	public String getMessageContent() {

		return textMessage;
	}

	@Override
	public String toString() {

		return textMessage;
	}
	
	

}
