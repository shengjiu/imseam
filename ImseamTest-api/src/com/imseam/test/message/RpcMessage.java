package com.imseam.test.message;

import com.imseam.test.Message;

public class RpcMessage extends Message {
	

	private static final long serialVersionUID = 5555800090496751469L;

	private int callbackId;


	public RpcMessage(int callbackId, String from, String target) {
		super(from, target);
		this.callbackId = callbackId;

	}


	public void setCallbackId(int callbackId) {
		this.callbackId = callbackId;
	}


	public int getCallbackId() {
		return callbackId;
	}


}
