package com.imseam.cdi.chatlet.deployment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.ejb.spi.EjbDescriptor;

import com.imseam.chatlet.IApplication;
import com.imseam.common.util.ClassUtil;

public class ChatAppBeanDeploymentArchive implements BeanDeploymentArchive {
	public static final String META_INF_BEANS_XML = "META-INF/beans.xml";
	public static final String CHAT_INF_BEANS_XML = "/CHAT-INF/beans.xml";

	private final List<String> classes;
	private final BeansXml beansXml;
	private final ServiceRegistry services;

	public ChatAppBeanDeploymentArchive(IApplication application, Bootstrap bootstrap) {
		this.services = new SimpleServiceRegistry();
		this.classes = new ArrayList<String>();
		List<URL> urls = new ArrayList<URL>();
		URLScanner scanner = new URLScanner(ClassUtil.getClassLoader());
		scanner.scanResources(new String[] { META_INF_BEANS_XML }, classes, urls);
		URL beans = application.getResourceURL(CHAT_INF_BEANS_XML);
		if (beans != null) {
			urls.add(beans);
		}

		this.beansXml = bootstrap.parse(urls);
	}

	public Collection<String> getBeanClasses() {
		return classes;
	}

	public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
		return Collections.emptySet();
	}

	public BeansXml getBeansXml() {
		return beansXml;
	}

	public Collection<EjbDescriptor<?>> getEjbs() {
		return Collections.emptySet();
	}

	public ServiceRegistry getServices() {
		return services;
	}

	public String getId() {
		// Use "flat" to allow us to continue to use ManagerObjectFactory
		return "flat";
	}

}
