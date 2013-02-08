package com.imseam.cdi.chatlet.spi;

import java.util.UUID;

import com.imseam.chatlet.listener.event.AbstractEvent;
import com.imseam.chatlet.listener.event.IEvent;


public class FuncBasedEvent extends AbstractEvent implements IEvent{

	private static final long serialVersionUID = -7832108067140566567L;

	public Func getFunc() {
		return func;
	}

	private String uid = "FuncBasedEvent:::" + UUID.randomUUID().toString();;
	private Func func;
	
	public FuncBasedEvent(Func func){
		assert(func != null);
		this.func = func;
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
