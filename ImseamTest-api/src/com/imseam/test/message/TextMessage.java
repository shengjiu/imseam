package com.imseam.test.message;

import com.imseam.test.Message;

public class TextMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3513051849687718439L;
	private String content; 

	
	public TextMessage(String content, String from, String windowId) {
		super(from, windowId);
		this.content = content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String windowId() {
		return this.getTargetId();
	}
	public String getContent() {
		return content;
	}
	
}
