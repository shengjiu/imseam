package com.imseam.test.message;

import com.imseam.test.Message;

public class WindowClosedMessage extends Message{
	

	private static final long serialVersionUID = -958538047365726724L;
	
	
	public WindowClosedMessage(String from, String windowId) {
		super(from, windowId);
	}

	
	public String windowId() {
		return this.getTargetId();
	}
	
}
