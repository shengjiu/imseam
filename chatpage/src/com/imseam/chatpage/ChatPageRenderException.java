package com.imseam.chatpage;

public class ChatPageRenderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2931704707057120974L;

	public ChatPageRenderException(String msg) {
		super(msg);
	}
	
	public ChatPageRenderException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ChatPageRenderException(Throwable cause) {
		super(cause);
	}
}
