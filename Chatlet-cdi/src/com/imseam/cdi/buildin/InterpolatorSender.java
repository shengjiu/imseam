package com.imseam.cdi.buildin;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IMessageSender;

public class InterpolatorSender implements IMessageSender{
	
	private IMessageSender sender;
	
	public InterpolatorSender(IMessageSender sender){
		this.sender = sender;
	}

	@Override
	public void send(IChatletMessage... responseMessages) {
		sender.send(responseMessages);
	}

	@Override
	public void send(String msg) {
		sender.send(msg);
		
	}

	@Override
	public void flush() {
		sender.flush();
	}
}
