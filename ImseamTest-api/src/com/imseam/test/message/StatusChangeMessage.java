package com.imseam.test.message;

import com.imseam.test.Message;

public class StatusChangeMessage  extends Message{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7881852077615418924L;

	private String status = null;
	
	public StatusChangeMessage(String from, String status) {
		super(from, null);
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
