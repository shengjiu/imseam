package com.imseam.chatlet.listener.event;

import java.util.UUID;

import com.imseam.chatlet.IChannel;

public class UserJoinWindowEvent extends AbstractEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5597722049221988704L;
	private IChannel channel;
	private String uid = "UserJoinWindowEvent:::" + UUID.randomUUID().toString();
	
	public UserJoinWindowEvent(){
		
	}
	
	public UserJoinWindowEvent(Object source, IChannel channel)
	{
		super(source);
		assert(channel != null);
		this.channel = channel;
	}
	
	public IChannel getChannel()
	{
		return channel;
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public UidType getUidType() {
		
		return UidType.SYSTEMEVENT;
	}


}
