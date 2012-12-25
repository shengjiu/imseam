package com.imseam.test.message;

import com.imseam.test.Message;

public class InvitationMessage extends Message{


	private static final long serialVersionUID = -5513829604238762172L;

	public InvitationMessage(String from, String targetId) {
		super(from, targetId);
		
	}

}
