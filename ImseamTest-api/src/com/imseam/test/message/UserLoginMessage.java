package com.imseam.test.message;

import com.imseam.test.Message;

public class UserLoginMessage extends Message{


	private static final long serialVersionUID = -5513829604238762172L;
	
	private String status = null;

	public UserLoginMessage(String username, String status) {
		super(username, null);
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public String username(){
		return this.getFrom();
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
