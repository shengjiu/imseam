package com.imseam.cdi.chatlet.spi;

import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;



public class LogCDIErrorCallback implements IEventErrorCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8997109091219158776L;
	private String eventSenderUid;
	private UidType senderIdType;
	
	private LogCDIErrorCallback(String eventSenderUid, UidType senderIdType){
		this.eventSenderUid = eventSenderUid;
		this.senderIdType = senderIdType;
	}
	
	private LogCDIErrorCallback(IWindow window){
		this.eventSenderUid = window.getUid();
		this.senderIdType = window.getUidType();
	}

	@Override
	public String getEventSenderUid() {
		return this.eventSenderUid;
	}

	@Override
	public UidType getSenderIdType() {
		return this.senderIdType;
	}

	@Override
	public void handleException(IContext senderContext, String sourceUid, Exception exp) {
		System.out.println("eventSenderUid:" + eventSenderUid);
		System.out.println("senderIdType:" + senderIdType);
		System.out.println("senderContext:" + senderContext);
		System.out.println("sourceUid:" + sourceUid);
		exp.printStackTrace();
	}
	
	public static LogCDIErrorCallback of(IWindow window){
		return new LogCDIErrorCallback(window);
	}

	public static LogCDIErrorCallback of(String eventSenderUid, UidType senderIdType){
		return new LogCDIErrorCallback(eventSenderUid, senderIdType);
	}

}
