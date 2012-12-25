package com.imseam.test.message;

import com.imseam.test.RemoteInvocation;

public class RpcRequestMessage extends RpcMessage {
	
	private static final long serialVersionUID = -5641859749562651492L;

	private RemoteInvocation remoteInvocation;

	public void setRemoteInvocation(RemoteInvocation remoteInvocation) {
		this.remoteInvocation = remoteInvocation;
	}


	public RpcRequestMessage(int callbackId, RemoteInvocation remoteInvocation, String from) {
		super(callbackId, from, null);
		this.remoteInvocation = remoteInvocation;

	}

	
	public RemoteInvocation getRemoteInvocation() {
		return remoteInvocation;
	}

	
}
