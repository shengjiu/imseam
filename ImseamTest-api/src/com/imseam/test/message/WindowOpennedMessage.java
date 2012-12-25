package com.imseam.test.message;

import com.imseam.test.Message;

public class WindowOpennedMessage extends Message{
	

	private static final long serialVersionUID = -958538047365726724L;
	
	private String [] buddyIds;

	
	public WindowOpennedMessage(String leader, String windowId, String... buddyIds) {
		super(leader, windowId);
		this.buddyIds = buddyIds;
	}

	
	public String windowId() {
		return this.getTargetId();
	}
	
	public String[] getBuddyIds(){
		return buddyIds;
	}

}
