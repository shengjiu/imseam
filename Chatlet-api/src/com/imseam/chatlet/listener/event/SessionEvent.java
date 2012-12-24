package com.imseam.chatlet.listener.event;

import java.util.UUID;

import com.imseam.chatlet.ISession;

public class SessionEvent extends AbstractEvent{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 792797874951559701L;
	private ISession session;
	private String uid = "SessionEvent:::" + UUID.randomUUID().toString();
	
	public SessionEvent(){
		
	}
	
	public ISession getSession() {
		return session;
	}

	public void setSession(ISession session) {
		this.session = session;
	}

	public SessionEvent(Object source, ISession session)
	{
		super(source);
		this.session = session;
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
