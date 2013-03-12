package com.imseam.test.connector.netty.invocation;

import com.imseam.test.RemoteInvocation;
import com.imseam.test.connector.netty.server.ServerRPCService;
import com.imseam.test.connector.netty.server.ServerRPCServiceFactory;

public class WindowStartedRemoteInvocation implements RemoteInvocation {


	private static final long serialVersionUID = -2218737058624339909L;
	private String windowId;
	private String username;

	
	public WindowStartedRemoteInvocation(){
		
	}
	
	public WindowStartedRemoteInvocation(String username, String windowId){
		this.username = username;
		this.windowId = windowId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getWindowId() {
		return windowId;
	}

	public void setWindowId(String windowId) {
		this.windowId = windowId;
	}

	@Override
	public Object invoke(Object parameter) {
		ServerRPCService service = ServerRPCServiceFactory.get().getService(username);
		service.windowStarted(windowId);
		return null;
	}
}
