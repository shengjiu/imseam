package com.imseam.cdi.chatlet.services;

import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;

import com.imseam.chatlet.IApplication;

public interface ChatletServices extends Service {

	/**
	 * Get the BDA for the current request. The ServletContext is provided for
	 * context.
	 * 
	 * @param ctx
	 * @return
	 */
	public BeanDeploymentArchive getBeanDeploymentArchive(IApplication app);

}