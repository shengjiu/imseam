package com.imseam.test.message;


public class RpcResponseMessage extends RpcMessage {
	

	public void setReturnedObject(Object returnedObject) {
		this.returnedObject = returnedObject;
	}

	private static final long serialVersionUID = 7415912424653216830L;

	private Object returnedObject;

	public RpcResponseMessage(int callbackId, Object returnedObject, String from) {
		super(callbackId, null, from);
		this.returnedObject = returnedObject;

	}

	public Object getReturnedObject() {
		return returnedObject;
	}

	
	
}
