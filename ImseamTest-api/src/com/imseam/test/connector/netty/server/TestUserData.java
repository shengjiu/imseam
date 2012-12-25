package com.imseam.test.connector.netty.server;

import org.jboss.netty.channel.Channel;


public class TestUserData {

	private String username;
	
	private String currentStatus;
	
	private Channel channel;
	
	public TestUserData(String username, String initStatus, Channel channel){
		this.username = username;
		this.currentStatus = initStatus;
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getUsername() {
		return username;
	}

	public void setStatus(String status) {
		this.currentStatus = status;
	}
	
	public String getStatus(){
		
		return currentStatus;
	}
	

}
