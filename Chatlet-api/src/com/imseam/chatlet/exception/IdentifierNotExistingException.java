package com.imseam.chatlet.exception;

import com.imseam.chatlet.IIdentiable.UidType;

public class IdentifierNotExistingException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7228230385628113188L;

	private String id;
	
	private UidType type;
	

	public IdentifierNotExistingException(UidType type, String id) {
		super(String.format("The %s (%s) doesn't exist", type, id));
		this.id = id;
		this.type = type;
	}
	
	public IdentifierNotExistingException(String msg, Throwable cause, UidType type, String id) {
		super(msg, cause);
		this.id = id;
		this.type = type;
	}
	
	public IdentifierNotExistingException(Throwable cause, UidType type, String id) {
		super(cause);
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public UidType getType() {
		return type;
	}
	
	
}
