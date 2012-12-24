package com.imseam.chatlet.listener.event;

import java.util.UUID;

import com.imseam.chatlet.IConnection;

public class ConnectionEvent extends AbstractEvent{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5533952437457384908L;
	
	private IConnection connection;
	
	private String uid = "ConnectionEvent:::" + UUID.randomUUID().toString();
	
	
	public ConnectionEvent(){
		
	}
	
	public ConnectionEvent(Object source, IConnection connection)
	{
		super(source);
		
		this.connection = connection;
		
	}
	
	public IConnection getConnection()
	{
		return connection;
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
