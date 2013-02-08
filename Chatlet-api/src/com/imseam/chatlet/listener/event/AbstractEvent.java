package com.imseam.chatlet.listener.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public abstract class AbstractEvent implements IEvent {

	private static final long serialVersionUID = -2873196125751276624L;
    
	/**
     * The object on which the Event initially occurred.
     */
    protected Object  source;

	private Map<String, Object> attributeMap = new HashMap<String, Object>();
	
	private Date timesttamp = new Date();
	
	public AbstractEvent(){
	}
	
	public AbstractEvent(Object source) {
		this.source = source;
	}

	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	public Set<String> getAttributeNames() {
		return attributeMap.keySet();
	}

	public Object removeAttribute(String name) {
		return attributeMap.remove(name);
	}

	public void setAttribute(String name, Object o) {
		attributeMap.put(name, o);
	}

	public void removeAllAttributes() {
		attributeMap.clear();
	}
	

    /**
     * The object on which the Event initially occurred.
     *
     * @return   The object on which the Event initially occurred.
     */
    public Object getSource() {
        return source;
    }

    /**
     * Returns a String representation of this EventObject.
     *
     * @return  A a String representation of this EventObject.
     */
    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
    
    @Override
	public Iterator<String> iterator() {
		return attributeMap.keySet().iterator();
	}
    
    @Override
    public Date getTimestamp(){
    	return this.timesttamp;
    }
	
	

}
