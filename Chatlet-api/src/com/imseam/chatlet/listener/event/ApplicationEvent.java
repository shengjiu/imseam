package com.imseam.chatlet.listener.event;

import java.util.UUID;

import com.imseam.chatlet.IApplication;

public class ApplicationEvent  extends AbstractEvent{

	private static final long serialVersionUID = -6386150108308604397L;

	private IApplication application;
	
	private String uid = "ApplicationEvent:::" + UUID.randomUUID().toString();
	
	public ApplicationEvent(){
		
	}

	public ApplicationEvent(Object source, IApplication application)
	{
		super(source);
		this.application = application;
	}
	
	public IApplication getApplication()
	{
		return application;
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
