package com.imseam.test.message;

import com.imseam.test.Message;

public class BuddyAddedToWindowMessage extends Message{
	private static final long serialVersionUID = -2391797767798025304L;
	private String[] buddyIds;

	
	public BuddyAddedToWindowMessage(String from, String windowId, String... buddyIds) {
		super(from, windowId);
		this.buddyIds = buddyIds;
	}

	
	public String windowId() {
		return this.getTargetId();
	}
	
	public String[] getBuddyIds(){
		return buddyIds;
	}


	public void setBuddyIds(String [] buddyIds) {
		this.buddyIds = buddyIds;
	}

}
