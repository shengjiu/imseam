package com.imseam.cdi.chatlet.event;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IConnection;

public class ConnectionRequest  implements IAttributes {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ConnectionRequest.class);
	private IAttributes attributes = new AttributesWrapper();
	private IConnection connection;
	private String buddyUid;
	
	public ConnectionRequest(){
		
	}
	
	public ConnectionRequest(IConnection connection, String buddyUid){
		this.connection = connection;
		this.buddyUid = buddyUid;
	}
	
	public String getBuddyUid() {
		return buddyUid;
	}

	public void setBuddyUid(String buddyUid) {
		this.buddyUid = buddyUid;
	}

	public IConnection getConnection() {
		return connection;
	}

	public void setConnection(IConnection connection) {
		this.connection = connection;
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

