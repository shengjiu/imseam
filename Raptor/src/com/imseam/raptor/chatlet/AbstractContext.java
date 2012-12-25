package com.imseam.raptor.chatlet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;

public abstract class AbstractContext implements IAttributes {
	
	private static Log log = LogFactory.getLog(AbstractContext.class);
	
	private Map<String, Object> attributeMap = null;
	

	
	protected AbstractContext(boolean threadSafe){
		log.debug(String.format("A %s ContextBase Object is creating.", threadSafe?"thread safe": "non thread safe"));
		if(threadSafe){
			attributeMap = new ConcurrentHashMap<String, Object>();
		}else{
			attributeMap = new HashMap<String, Object>();
		}
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
		if(o != null)
			attributeMap.put(name, o);
	}

	public void removeAllAttributes() {
		attributeMap.clear();
	}
	
	//to be overrided
	public void flush(){}
	
	@Override
	public Iterator<String> iterator() {

		return attributeMap.keySet().iterator();
	}

}
