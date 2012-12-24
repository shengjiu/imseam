package com.imseam.cdi.chatlet.event;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IWindow;

public class WindowErrorCallbackRequest  implements IAttributes {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(WindowErrorCallbackRequest.class);
	private IAttributes attributes = new AttributesWrapper();
	private IWindow window;
	private Exception exp;
	private String expSourceUid;
	
	public WindowErrorCallbackRequest(){
		
	}
	public WindowErrorCallbackRequest(IWindow window, String expSourceUid, Exception exp){
		this.window = window;
		this.exp = exp;
		this.expSourceUid = expSourceUid;
	}

	public Exception getExp() {
		return exp;
	}

	public String getExpSourceUid() {
		return expSourceUid;
	}
	
	public IWindow getWindow() {
		return window;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.getAttribute(name);
	}

	@Override
	public Set<String> getAttributeNames() {
		return attributes.getAttributeNames();
	}

	@Override
	public Object removeAttribute(String name) {
		return attributes.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.setAttribute(name, o);
	}

	@Override
	public void removeAllAttributes() {
		attributes.removeAllAttributes();
	}
	
	@Override
	public Iterator<String> iterator() {
		return attributes.iterator();
	}
	
}

