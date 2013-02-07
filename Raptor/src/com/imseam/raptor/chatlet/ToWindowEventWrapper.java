package com.imseam.raptor.chatlet;

import java.util.Iterator;
import java.util.Set;

import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.event.IEvent;

public class ToWindowEventWrapper implements IEvent {

	private static final long serialVersionUID = -1216124971557734349L;
	
	private IWindow window;
	private IEvent event;
	
	
	public ToWindowEventWrapper(IWindow window, IEvent event){
		this.window = window;
		this.event = event;
	}
	
	public IWindow getWindow() {
		return window;
	}

	public IEvent getEvent() {
		return event;
	}

	@Override
	public Object getAttribute(String name) {
		
		return event.getAttribute(name);
	}

	@Override
	public Set<String> getAttributeNames() {
		return event.getAttributeNames();
	}

	@Override
	public Object removeAttribute(String name) {
		return event.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		event.setAttribute(name, o);
	}

	@Override
	public void removeAllAttributes() {
		event.removeAllAttributes();
	}

	@Override
	public Iterator<String> iterator() {
		return event.iterator();
	}

	@Override
	public String getUid() {
		return event.getUid();
	}

	@Override
	public UidType getUidType() {
		return event.getUidType();
	}

	@Override
	public Object getSource() {
		// TODO Auto-generated method stub
		return null;
	}

}
