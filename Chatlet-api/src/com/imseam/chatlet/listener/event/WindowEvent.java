package com.imseam.chatlet.listener.event;

import java.util.UUID;

import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IWindow;

public class WindowEvent extends AbstractEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4774921862344554498L;
	private IConnection connectionContext;
	private IWindow window;
	private String uid = "WindowEvent:::" + UUID.randomUUID().toString();

	public WindowEvent(){
		
	}
	
	public WindowEvent(Object source, IConnection connectionContext, IWindow window)
	{
		super(source);
		this.connectionContext = connectionContext;
		this.window = window;
	}
	
	public IConnection getConnection()
	{
		return connectionContext;
	}
	
	public IWindow getWindow()
	{
		return window;
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
