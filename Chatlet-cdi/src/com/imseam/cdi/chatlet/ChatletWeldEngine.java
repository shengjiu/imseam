package com.imseam.cdi.chatlet;

import java.util.HashSet;
import java.util.Set;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.Container;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.injection.spi.ResourceInjectionServices;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.manager.api.WeldManager;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory;

import com.imseam.cdi.chatlet.deployment.ChatletDeployment;
import com.imseam.cdi.chatlet.services.ChatletResourceInjectionServices;
import com.imseam.cdi.chatlet.services.ChatletServices;
import com.imseam.cdi.chatlet.services.ChatletServicesImpl;
import com.imseam.chatlet.IApplication;

public class ChatletWeldEngine {

	private static final Log log = LogFactory.getLog(ChatletWeldEngine.class);

	private static final String BOOTSTRAP_IMPL_CLASS_NAME = "com.imseam.cdi.chatlet.ChatletBootstrap";

//	private static final String APPLICATION_BEAN_STORE_ATTRIBUTE_NAME = ChatletWeldEngine.class.getName() + ".applicationBeanStore";
	
    public static final String BEAN_MANAGER_ATTRIBUTE_NAME = ChatletWeldEngine.class.getPackage().getName() + "." + BeanManager.class.getName();


//	private static final String EXPRESSION_FACTORY_NAME = "org.jboss.weld.el.ExpressionFactory";

	private Bootstrap bootstrap;
	private WeldManager manager;

	private ChatletLifecycle lifecycle;

	private ChatletLifecycle getLifecycle(IApplication application) {
		if (lifecycle == null) {
			this.lifecycle = new ChatletLifecycle(application);
		}
		return lifecycle;
	}
	
	public WeldManager getManager() {
		return manager;
	}

	private static BeanManagerImpl getBeanManager(IApplication application) {

		BeanDeploymentArchive car = Container.instance().services().get(ChatletServices.class).getBeanDeploymentArchive(application);
		if (car == null) {
			throw new IllegalStateException("Missing BEAN_DEPLOYMENT_ARCHIVE");
		}
		BeanManagerImpl beanManager = Container.instance().beanDeploymentArchives().get(car);
		if (beanManager == null) {
			throw new IllegalStateException("BEAN_MANAGER_FOR_ARCHIVE_NOT_FOUND");
		}
		return beanManager;
	}
  

	public void initialize(IApplication application) {
		try {
			//bootstrap = ClassUtil.createInstance(BOOTSTRAP_IMPL_CLASS_NAME);
			bootstrap = new ChatletBootstrap();
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Error loading Weld bootstrap, check that Weld is on the classpath", e);
		}
		
		ProxyFactory.classLoaderProvider = new ClassLoaderProvider() {

			public ClassLoader get(ProxyFactory pf) {
				return Thread.currentThread().getContextClassLoader();
			}

		};
//		

//		BeanStore applicationBeanStore = new ConcurrentHashMapBeanStore();
//
//		application.setAttribute(APPLICATION_BEAN_STORE_ATTRIBUTE_NAME, applicationBeanStore);

		ChatletDeployment deployment = new ChatletDeployment(application, bootstrap);
		try {
			deployment.getChatAppBeanDeploymentArchive().getServices().add(ResourceInjectionServices.class, new ChatletResourceInjectionServices() {
			});
		} catch (NoClassDefFoundError ed) {
			// Support GAE
			log.warn("@Resource injection not available in simple beans", ed);
		}

		deployment.getServices().add(ChatletServices.class, new ChatletServicesImpl(deployment.getChatAppBeanDeploymentArchive()));
		deployment.getServices().add(ChatletLifecycle.class, this.getLifecycle(application));


		Environment environment = new ChatletEnvironment().
				addRequiredDeploymentService(ResourceLoader.class).
				addRequiredDeploymentService(ChatletServices.class).
				addRequiredDeploymentService(ScheduledExecutorServiceFactory.class).
				addRequiredBeanDeploymentArchiveService(ResourceInjectionServices.class);
//				addRequiredDeploymentService(TransactionServices.class).
//				addRequiredDeploymentService(ResourceLoader.class).
//				addRequiredDeploymentService(SecurityServices.class).
//				addRequiredDeploymentService(ValidationServices.class).
//				addRequiredDeploymentService(ChatletServices.class).
//				addRequiredDeploymentService(EjbServices.class).
//				addRequiredDeploymentService(ScheduledExecutorServiceFactory.class).
//				addRequiredBeanDeploymentArchiveService(JpaInjectionServices.class).
//				addRequiredBeanDeploymentArchiveService(ResourceInjectionServices.class).
//				addRequiredBeanDeploymentArchiveService(EjbInjectionServices.class);

		bootstrap.startContainer(environment, deployment).startInitialization();
		manager = bootstrap.getManager(deployment.getChatAppBeanDeploymentArchive());
		

        // Push the manager into the servlet context so we can access in JSF
        application.setAttribute(BEAN_MANAGER_ATTRIBUTE_NAME, manager);

		bootstrap.deployBeans().validateBeans().endInitialization();

		if (!Container.available()) {
			log.warn("Weld container is not available");
			return;
		}
		if (!Container.instance().services().contains(ChatletServices.class)) {
			throw new IllegalStateException("Chatlet services is not available");
		}

		application.setAttribute(BeanManager.class.getName(), getBeanManager(application));
//		application.setAttribute(BeanManager.class.getName(), manager);

	}

	public void applicationStopped(IApplication application) {
		bootstrap.shutdown();

		application.removeAttribute(BeanManager.class.getName());
	}

	public static class ChatletEnvironment implements Environment {

		private final Set<Class<? extends Service>> requiredDeploymentServices;

		private final Set<Class<? extends Service>> requiredBeanDeploymentArchiveServices;

		private ChatletEnvironment() {
			this.requiredBeanDeploymentArchiveServices = new HashSet<Class<? extends Service>>();
			this.requiredDeploymentServices = new HashSet<Class<? extends Service>>();
		}

		public Set<Class<? extends Service>> getRequiredBeanDeploymentArchiveServices() {
			return requiredBeanDeploymentArchiveServices;
		}

		public Set<Class<? extends Service>> getRequiredDeploymentServices() {
			return requiredDeploymentServices;
		}

		private ChatletEnvironment addRequiredDeploymentService(Class<? extends Service> service) {
			this.requiredDeploymentServices.add(service);
			return this;
		}

		private ChatletEnvironment addRequiredBeanDeploymentArchiveService(Class<? extends Service> service) {
			this.requiredBeanDeploymentArchiveServices.add(service);
			return this;
		}

	}

}
