package com.imseam.chatlet;

import java.util.Iterator;
import java.util.Set;

public interface IAttributes {

	Object getAttribute(String name);

	Set<String> getAttributeNames();

	Object removeAttribute(String name);

	void setAttribute(String name, Object o);
	
	void removeAllAttributes();
	
	Iterator<String> iterator();
	
}
