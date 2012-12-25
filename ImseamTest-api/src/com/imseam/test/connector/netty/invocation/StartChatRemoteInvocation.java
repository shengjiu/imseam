package com.imseam.test.connector.netty.invocation;

import com.imseam.test.RemoteInvocation;
import com.imseam.test.connector.netty.server.ServerRPCService;
import com.imseam.test.connector.netty.server.ServerRPCServiceFactory;

public class StartChatRemoteInvocation implements RemoteInvocation {



	private static final long serialVersionUID = -5607520768083922910L;
	private String username;
	private String[] buddies;
	
	public StartChatRemoteInvocation(){
		
	}
	
	public StartChatRemoteInvocation(String username, String... buddies){
		this.username = username;
		this.buddies = buddies;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getBuddies() {
		return buddies;
	}

	public void setBuddies(String[] buddies) {
		this.buddies = buddies;
	}

	@Override
	public String invoke(Object parameter) {
		ServerRPCService service = ServerRPCServiceFactory.get().getService(username);
		
		return service.startWindow(buddies);
	}
}
