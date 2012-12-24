package com.imseam.cdi.context.beanstore;

import java.util.Collection;

import org.jboss.weld.context.beanstore.AttributeBeanStore;
import org.jboss.weld.context.beanstore.LockStore;
import org.jboss.weld.context.beanstore.NamingScheme;

import com.imseam.chatlet.IAttributes;

public class IMRequestBeanStore extends AttributeBeanStore {

		private final IAttributes request;

	    public IMRequestBeanStore(IAttributes request, NamingScheme namingScheme) {
	        super(namingScheme);
	        this.request = request;
	    }

	    @Override
	    protected Object getAttribute(String key) {
	        return request.getAttribute(key);
	    }

	    @Override
	    protected void removeAttribute(String key) {
	        request.removeAttribute(key);
	    }

	    @Override
	    protected Collection<String> getAttributeNames() {
	        return request.getAttributeNames();
	    }
	 
	    @Override
	    protected void setAttribute(String key, Object instance) {
	        request.setAttribute(key, instance);
	    }

	    @Override
	    public boolean attach() {
	        // Doesn't support detachment
	        return false;
	    }

	    @Override
	    public boolean detach() {
	        return false;
	    }

	    @Override
	    public boolean isAttached() {
	        // Doesn't support detachment
	        return true;
	    }

	    @Override
	    public LockStore getLockStore() {
	        return null;
	    }

	}