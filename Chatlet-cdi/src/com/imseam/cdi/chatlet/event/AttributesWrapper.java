package com.imseam.cdi.chatlet.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.imseam.chatlet.IAttributes;

class AttributesWrapper implements IAttributes{
	
	private Map<String, Object> attributeMap = new HashMap<String, Object>();
	
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

	@Override
	public Iterator<String> iterator() {
		return attributeMap.keySet().iterator();
	}
	
}