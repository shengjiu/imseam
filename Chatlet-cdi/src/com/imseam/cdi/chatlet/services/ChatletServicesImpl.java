package com.imseam.cdi.chatlet.services;

import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;

import com.imseam.chatlet.IApplication;

public class ChatletServicesImpl implements ChatletServices {

	private final BeanDeploymentArchive beanDeploymentArchive;

	public ChatletServicesImpl(BeanDeploymentArchive beanDeploymentArchive) {
		this.beanDeploymentArchive = beanDeploymentArchive;
	}

	public BeanDeploymentArchive getBeanDeploymentArchive(IApplication ctx) {
		return beanDeploymentArchive;
	}

	public void cleanup() {
	}

}