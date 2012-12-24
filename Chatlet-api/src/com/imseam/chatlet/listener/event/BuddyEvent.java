package com.imseam.chatlet.listener.event;

import java.util.UUID;

import com.imseam.chatlet.IBuddy;
import com.imseam.chatlet.IConnection;

public class BuddyEvent extends AbstractEvent{

	private static final long serialVersionUID = -2616018502756503259L;
	
	private IConnection connection;
	private IBuddy buddy;
	
	private String uid = "BuddyEvent:::" + UUID.randomUUID().toString();
	
	public BuddyEvent(){
		
	}

	public BuddyEvent(Object source, IConnection connection, IBuddy buddy)
	{
		super(source);
		this.connection = connection;
		this.buddy = buddy;
	}
	
	public IConnection getConnection()
	{
		return connection;
	}
	
	public IBuddy getBuddy()
	{
		return buddy;
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
