package com.imseam.common.exception;

import java.util.UUID;

public class TracableRuntimeException extends  RuntimeException{

	private static final long serialVersionUID = 7526332166098236705L;

	protected String uniqueID = UUID.randomUUID().toString();

	public TracableRuntimeException(String msg) {
		super(msg);
	}
	
	public TracableRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public TracableRuntimeException(Throwable cause) {
		super(cause);

	}
	
	public String getUniqueID() {
		return uniqueID;
	}

}