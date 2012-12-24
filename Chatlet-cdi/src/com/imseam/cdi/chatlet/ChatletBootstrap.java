package com.imseam.cdi.chatlet;

import static org.jboss.weld.logging.Category.BOOTSTRAP;
import static org.jboss.weld.logging.Category.VERSION;
import static org.jboss.weld.logging.LoggerFactory.loggerFactory;
import static org.jboss.weld.logging.messages.BootstrapMessage.DEPLOYMENT_ARCHIVE_NULL;
import static org.jboss.weld.logging.messages.BootstrapMessage.DEPLOYMENT_REQUIRED;
import static org.jboss.weld.logging.messages.BootstrapMessage.JTA_UNAVAILABLE;
import static org.jboss.weld.logging.messages.BootstrapMessage.MANAGER_NOT_INITIALIZED;
import static org.jboss.weld.logging.messages.BootstrapMessage.UNSPECIFIED_REQUIRED_SERVICE;
import static org.jboss.weld.logging.messages.BootstrapMessage.VALIDATING_BEANS;
import static org.jboss.weld.manager.Enabled.EMPTY_ENABLED;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.Container;
import org.jboss.weld.ContainerState;
import org.jboss.weld.bean.RIBean;
import org.jboss.weld.bean.builtin.BeanManagerBean;
import org.jboss.weld.bean.builtin.ContextBean;
import org.jboss.weld.bean.proxy.util.SimpleProxyServices;
import org.jboss.weld.bootstrap.BeanDeployment;
import org.jboss.weld.bootstrap.ContextHolder;
import org.jboss.weld.bootstrap.ExtensionBeanDeployer;
import org.jboss.weld.bootstrap.Validator;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environment;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.ServiceRegistries;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.events.AfterBeanDiscoveryImpl;
import org.jboss.weld.bootstrap.events.AfterDeploymentValidationImpl;
import org.jboss.weld.bootstrap.events.BeforeBeanDiscoveryImpl;
import org.jboss.weld.bootstrap.events.BeforeShutdownImpl;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.context.ApplicationContext;
import org.jboss.weld.context.DependentContext;
import org.jboss.weld.context.SingletonContext;
import org.jboss.weld.context.bound.BoundLiteral;
import org.jboss.weld.context.ejb.EjbLiteral;
import org.jboss.weld.context.ejb.EjbRequestContext;
import org.jboss.weld.context.ejb.EjbRequestContextImpl;
import org.jboss.weld.context.unbound.UnboundLiteral;
import org.jboss.weld.ejb.spi.EjbServices;
import org.jboss.weld.exceptions.IllegalArgumentException;
import org.jboss.weld.exceptions.IllegalStateException;
import org.jboss.weld.injection.CurrentInjectionPoint;
import org.jboss.weld.logging.messages.VersionMessage;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.manager.InjectionTargetValidator;
import org.jboss.weld.metadata.TypeStore;
import org.jboss.weld.metadata.cache.MetaAnnotationStore;
import org.jboss.weld.resources.ClassTransformer;
import org.jboss.weld.resources.DefaultResourceLoader;
import org.jboss.weld.resources.SharedObjectCache;
import org.jboss.weld.resources.SingleThreadScheduledExecutorServiceFactory;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory;
import org.jboss.weld.serialization.ContextualStoreImpl;
import org.jboss.weld.serialization.spi.ContextualStore;
import org.jboss.weld.serialization.spi.ProxyServices;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.jboss.weld.util.ServiceLoader;
import org.jboss.weld.util.reflection.Formats;
import org.jboss.weld.util.reflection.instantiation.InstantiatorFactory;
import org.jboss.weld.util.reflection.instantiation.LoaderInstantiatorFactory;
import org.jboss.weld.xml.BeansXmlParser;
import org.slf4j.cal10n.LocLogger;

import com.imseam.cdi.context.IMApplicationContext;
import com.imseam.cdi.context.IMChannelContext;
import com.imseam.cdi.context.IMConnectionContext;
import com.imseam.cdi.context.IMDependentContext;
import com.imseam.cdi.context.IMMeetingContext;
import com.imseam.cdi.context.IMRequestContext;
import com.imseam.cdi.context.IMSessionContext;
import com.imseam.cdi.context.IMSingletonContext;
import com.imseam.cdi.context.IMWindowContext;


public class ChatletBootstrap implements Bootstrap {


	



	   
	   
//		protected void initializeContexts() {
//			ChatletLifecycle lifecycle = Container.instance().services().get(ChatletLifecycle.class); 
//
//			Container.instance().deploymentManager().addContext(lifecycle.getDependentContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getRequestContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getWindowContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getChannelContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getMeetingContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getConnectionContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getSessionContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getApplicationContext());
//			Container.instance().deploymentManager().addContext(lifecycle.getSingletonContext());
//
//		}	  
//		
//		protected void createContexts() {
//		}
	   
    private static final LocLogger log = loggerFactory().getLogger(BOOTSTRAP);

    /**
     * A Deployment visitor which can find the transitive closure of Bean
     * Deployment Archives
     *
     * @author pmuir
     */
    private static class DeploymentVisitor {

        private final BeanManagerImpl deploymentManager;
        private final Environment environment;
        private final Deployment deployment;
        private final Map<BeanDeploymentArchive, BeanDeployment> managerAwareBeanDeploymentArchives;
        private final Collection<ContextHolder<? extends Context>> contexts;

        public DeploymentVisitor(BeanManagerImpl deploymentManager, Environment environment, final Deployment deployment, Collection<ContextHolder<? extends Context>> contexts) {
            this.deploymentManager = deploymentManager;
            this.environment = environment;
            this.deployment = deployment;
            this.contexts = contexts;
            this.managerAwareBeanDeploymentArchives = new ConcurrentHashMap<BeanDeploymentArchive, BeanDeployment>();
        }

        public Map<BeanDeploymentArchive, BeanDeployment> visit() {
            for (BeanDeploymentArchive archvive : deployment.getBeanDeploymentArchives()) {
                visit(archvive, managerAwareBeanDeploymentArchives, new HashSet<BeanDeploymentArchive>(), true);
            }
            return managerAwareBeanDeploymentArchives;
        }

        private <T extends Service> void copyService(BeanDeploymentArchive archive, Class<T> serviceClass) {
            // for certain services we can fall back to deployment-level settings or defaults
            ServiceRegistry registry = archive.getServices();
            if (registry.contains(serviceClass) == false) {
                T service = deployment.getServices().get(serviceClass);
                if (service != null)
                    registry.add(serviceClass, service);
            }
        }

        private BeanDeployment visit(BeanDeploymentArchive beanDeploymentArchive, Map<BeanDeploymentArchive, BeanDeployment> managerAwareBeanDeploymentArchives, Set<BeanDeploymentArchive> seenBeanDeploymentArchives, boolean validate) {
            copyService(beanDeploymentArchive, ResourceLoader.class);
            copyService(beanDeploymentArchive, InstantiatorFactory.class);
            // Check that the required services are specified
            if (validate) {
                verifyServices(beanDeploymentArchive.getServices(), environment.getRequiredBeanDeploymentArchiveServices());
            }

            // Check the id is not null
            if (beanDeploymentArchive.getId() == null) {
                throw new IllegalArgumentException(DEPLOYMENT_ARCHIVE_NULL, beanDeploymentArchive);
            }

            BeanDeployment parent = managerAwareBeanDeploymentArchives.get(beanDeploymentArchive);
            if (parent == null) {
                // Create the BeanDeployment
                parent = new BeanDeployment(beanDeploymentArchive, deploymentManager, deployment.getServices(), contexts);

                // Attach it
                managerAwareBeanDeploymentArchives.put(beanDeploymentArchive, parent);
            }
            seenBeanDeploymentArchives.add(beanDeploymentArchive);
            for (BeanDeploymentArchive archive : beanDeploymentArchive.getBeanDeploymentArchives()) {
                BeanDeployment child;
                // Cut any circularties
                if (!seenBeanDeploymentArchives.contains(archive)) {
                    child = visit(archive, managerAwareBeanDeploymentArchives, seenBeanDeploymentArchives, validate);
                } else {
                    // already visited
                    child = managerAwareBeanDeploymentArchives.get(archive);
                }
                parent.getBeanManager().addAccessibleBeanManager(child.getBeanManager());
            }
            return parent;
        }

    }

    static {
        loggerFactory().getLogger(VERSION).info(VersionMessage.VERSION, Formats.version(WeldBootstrap.class.getPackage()));
    }

    // The Bean manager
    private BeanManagerImpl deploymentManager;
    private Map<BeanDeploymentArchive, BeanDeployment> beanDeployments;
    private Environment environment;
    private Deployment deployment;
    private DeploymentVisitor deploymentVisitor;
    private final BeansXmlParser beansXmlParser;
    private Collection<ContextHolder<? extends Context>> contexts;

    public ChatletBootstrap() {
        this.beansXmlParser = new BeansXmlParser();
    }

    public Bootstrap startContainer(Environment environment, Deployment deployment) {
        synchronized (this) {
            if (deployment == null) {
                throw new IllegalArgumentException(DEPLOYMENT_REQUIRED);
            }
            final ServiceRegistry registry = deployment.getServices();
            if (!registry.contains(ResourceLoader.class)) {
                registry.add(ResourceLoader.class, DefaultResourceLoader.INSTANCE);
            }
            if (!registry.contains(InstantiatorFactory.class)) {
                registry.add(InstantiatorFactory.class, new LoaderInstantiatorFactory());
            }
            if (!registry.contains(ScheduledExecutorServiceFactory.class)) {
                registry.add(ScheduledExecutorServiceFactory.class, new SingleThreadScheduledExecutorServiceFactory());
            }
            if (!registry.contains(ProxyServices.class)) {
                registry.add(ProxyServices.class, new SimpleProxyServices());
            }

            verifyServices(registry, environment.getRequiredDeploymentServices());

            if (!registry.contains(TransactionServices.class)) {
                log.info(JTA_UNAVAILABLE);
            }
            // TODO Reinstate if we can find a good way to detect.
            // if (!deployment.getServices().contains(EjbServices.class))
            // {
            // log.info("EJB services not available. Session beans will be simple beans, CDI-style injection into non-contextual EJBs, injection of remote EJBs and injection of @EJB in simple beans will not be available");
            // }
            // if (!deployment.getServices().contains(JpaInjectionServices.class))
            // {
            // log.info("JPA services not available. Injection of @PersistenceContext will not occur. Entity beans will be discovered as simple beans.");
            // }
            // if
            // (!deployment.getServices().contains(ResourceInjectionServices.class))
            // {
            // log.info("@Resource injection not available.");
            // }

            this.deployment = deployment;
            ServiceRegistry implementationServices = getImplementationServices();

            registry.addAll(implementationServices.entrySet());

            ServiceRegistry deploymentServices = new SimpleServiceRegistry();
            deploymentServices.add(ClassTransformer.class, implementationServices.get(ClassTransformer.class));
            deploymentServices.add(MetaAnnotationStore.class, implementationServices.get(MetaAnnotationStore.class));
            deploymentServices.add(TypeStore.class, implementationServices.get(TypeStore.class));
            deploymentServices.add(ContextualStore.class, implementationServices.get(ContextualStore.class));

            this.environment = environment;
            this.deploymentManager = BeanManagerImpl.newRootManager("deployment", deploymentServices, EMPTY_ENABLED);

            Container.initialize(deploymentManager, ServiceRegistries.unmodifiableServiceRegistry(registry));
            Container.instance().setState(ContainerState.STARTING);

            this.contexts = createContexts(deploymentServices);
            this.deploymentVisitor = new DeploymentVisitor(deploymentManager, environment, deployment, contexts);

            // Read the deployment structure, this will be the physical structure
            // as caused by the presence of beans.xml
            beanDeployments = deploymentVisitor.visit();

            return this;
        }
    }

    private ServiceRegistry getImplementationServices() {
        ServiceRegistry services = new SimpleServiceRegistry();
        // Temporary workaround to provide context for building annotated class
        // TODO expose AnnotatedClass on SPI and allow container to provide impl
        // of this via ResourceLoader
        services.add(Validator.class, new Validator());
        TypeStore typeStore = new TypeStore();
        services.add(TypeStore.class, typeStore);
        ClassTransformer classTransformer = new ClassTransformer(typeStore);
        services.add(ClassTransformer.class, classTransformer);
        services.add(SharedObjectCache.class, new SharedObjectCache());
        services.add(MetaAnnotationStore.class, new MetaAnnotationStore(classTransformer));
        services.add(ContextualStore.class, new ContextualStoreImpl());
        services.add(CurrentInjectionPoint.class, new CurrentInjectionPoint());
        return services;
    }

    public BeanManagerImpl getManager(BeanDeploymentArchive beanDeploymentArchive) {
        synchronized (this) {
            BeanDeployment beanDeployment = beanDeployments.get(beanDeploymentArchive);
            if (beanDeployment != null) {
                return beanDeployment.getBeanManager().getCurrent();
            } else {
                return null;
            }
        }
    }

    public Bootstrap startInitialization() {
        synchronized (this) {
            if (deploymentManager == null) {
                throw new IllegalStateException(MANAGER_NOT_INITIALIZED);
            }

            ExtensionBeanDeployer extensionBeanDeployer = new ExtensionBeanDeployer(deploymentManager, deployment, beanDeployments, contexts);
            extensionBeanDeployer.addExtensions(deployment.getExtensions());
            extensionBeanDeployer.deployBeans();

            // Add the Deployment BeanManager Bean to the Deployment BeanManager
            deploymentManager.addBean(new BeanManagerBean(deploymentManager));

            // Re-Read the deployment structure, this will be the physical
            // structure, and will add in BDAs for any extensions outside a
            // physical BDA
            beanDeployments = deploymentVisitor.visit();

            BeforeBeanDiscoveryImpl.fire(deploymentManager, deployment, beanDeployments, contexts);

            // Re-Read the deployment structure, this will be the physical
            // structure, extensions and any classes added using addAnnotatedType
            // outside the physical BDA
            beanDeployments = deploymentVisitor.visit();

        }
        return this;
    }

    public Bootstrap deployBeans() {
        synchronized (this) {
            for (Entry<BeanDeploymentArchive, BeanDeployment> entry : beanDeployments.entrySet()) {
                entry.getValue().createBeans(environment);
            }
            for (Entry<BeanDeploymentArchive, BeanDeployment> entry : beanDeployments.entrySet()) {
                entry.getValue().deploySpecialized(environment);
            }
            // TODO keep a list of new bdas, add them all in, and deploy beans for
            // them, then merge into existing
            for (Entry<BeanDeploymentArchive, BeanDeployment> entry : beanDeployments.entrySet()) {
                entry.getValue().deployBeans(environment);
            }
            AfterBeanDiscoveryImpl.fire(deploymentManager, deployment, beanDeployments, contexts);
            // Re-read the deployment structure, this will be the physical
            // structure, extensions, classes, and any beans added using addBean
            // outside the physical structure
            beanDeployments = deploymentVisitor.visit();
            for (Entry<BeanDeploymentArchive, BeanDeployment> entry : beanDeployments.entrySet()) {
                entry.getValue().afterBeanDiscovery(environment);
            }
            Container.instance().putBeanDeployments(beanDeployments);
            Container.instance().setState(ContainerState.INITIALIZED);
        }
        return this;
    }

    public Bootstrap validateBeans() {
        synchronized (this) {
            log.debug(VALIDATING_BEANS);
            for (Entry<BeanDeploymentArchive, BeanDeployment> entry : beanDeployments.entrySet()) {
                BeanManagerImpl beanManager = entry.getValue().getBeanManager();
                beanManager.getBeanResolver().clear();
                deployment.getServices().get(Validator.class).validateDeployment(beanManager, entry.getValue().getBeanDeployer().getEnvironment());
                beanManager.getServices().get(InjectionTargetValidator.class).validate();
            }
            AfterDeploymentValidationImpl.fire(deploymentManager, beanDeployments);
        }
        return this;
    }

    public Bootstrap endInitialization() {
        // TODO rebuild the manager accessibility graph if the bdas have changed
        synchronized (this) {
            // Register the managers so external requests can handle them
            Container.instance().setState(ContainerState.VALIDATED);
            // clear the TypeSafeResolvers, so data that is only used at startup
            // is not kept around using up memory
            deploymentManager.getBeanResolver().clear();
            deploymentManager.getAccessibleObserverNotifier().clear();
            deploymentManager.getDecoratorResolver().clear();
            for (Entry<BeanDeploymentArchive, BeanDeployment> entry : beanDeployments.entrySet()) {
                BeanManagerImpl beanManager = entry.getValue().getBeanManager();
                beanManager.getBeanResolver().clear();
                beanManager.getAccessibleObserverNotifier().clear();
                beanManager.getDecoratorResolver().clear();
                for (Bean<?> bean : beanManager.getBeans()) {
                    if (bean instanceof RIBean<?>) {
                        RIBean<?> riBean = (RIBean<?>) bean;
                        riBean.cleanupAfterBoot();
                    }
                }
                beanManager.getClosure().clear();
            }
        }
        return this;
    }

    protected Collection<ContextHolder<? extends Context>> createContexts(ServiceRegistry services) {
        List<ContextHolder<? extends Context>> contexts = new ArrayList<ContextHolder<? extends Context>>();

        /*
        * Register a full set of bound and unbound contexts. Although we may not use all of
        * these (e.g. if we are running in a servlet environment) they may be
        * useful for an application.
        */
        contexts.add(new ContextHolder<ApplicationContext>(new IMApplicationContext(), ApplicationContext.class, UnboundLiteral.INSTANCE));
        contexts.add(new ContextHolder<SingletonContext>(new IMSingletonContext(), SingletonContext.class, UnboundLiteral.INSTANCE));
        contexts.add(new ContextHolder<IMSessionContext>(new IMSessionContext(), IMSessionContext.class, BoundLiteral.INSTANCE));
        contexts.add(new ContextHolder<IMWindowContext>(new IMWindowContext(), IMWindowContext.class, BoundLiteral.INSTANCE));
        contexts.add(new ContextHolder<IMRequestContext>(new IMRequestContext(), IMRequestContext.class, BoundLiteral.INSTANCE));
        contexts.add(new ContextHolder<IMMeetingContext>(new IMMeetingContext(), IMMeetingContext.class, BoundLiteral.INSTANCE));
        contexts.add(new ContextHolder<IMConnectionContext>(new IMConnectionContext(), IMConnectionContext.class, BoundLiteral.INSTANCE));
        contexts.add(new ContextHolder<IMChannelContext>(new IMChannelContext(), IMChannelContext.class, BoundLiteral.INSTANCE));
        contexts.add(new ContextHolder<DependentContext>(new IMDependentContext(services.get(ContextualStore.class)), DependentContext.class, UnboundLiteral.INSTANCE));

//        if (Reflections.isClassLoadable("javax.servlet.ServletContext", deployment.getServices().get(ResourceLoader.class))) {
//            // Register the Http contexts if not in
//            contexts.add(new ContextHolder<HttpSessionContext>(new HttpSessionContextImpl(), HttpSessionContext.class, HttpLiteral.INSTANCE));
//            contexts.add(new ContextHolder<HttpConversationContext>(new HttpConversationContextImpl(), HttpConversationContext.class, HttpLiteral.INSTANCE));
//            contexts.add(new ContextHolder<HttpRequestContext>(new HttpRequestContextImpl(), HttpRequestContext.class, HttpLiteral.INSTANCE));
//        }

        if (deployment.getServices().contains(EjbServices.class)) {
            // Register the EJB Request context if EjbServices are available
            contexts.add(new ContextHolder<EjbRequestContext>(new EjbRequestContextImpl(), EjbRequestContext.class, EjbLiteral.INSTANCE));
        }

        /*
        * Register the contexts with the bean manager and add the beans to the
        * deployment manager so that they are easily accessible (contexts are app
        * scoped)
        */
        for (ContextHolder<? extends Context> context : contexts) {
            deploymentManager.addContext(context.getContext());
            deploymentManager.addBean(ContextBean.of(context, deploymentManager));
        }


        return contexts;
    }

    public void shutdown() {
        synchronized (this) {
            try {
                ApplicationContext applicationContext = deploymentManager.instance().select(ApplicationContext.class).get();
                try {
                    BeforeShutdownImpl.fire(deploymentManager, beanDeployments);
                } finally {
                    applicationContext.invalidate();
                }
            } finally {
                Container.instance().setState(ContainerState.SHUTDOWN);
                Container.instance().cleanup();
            }
        }
    }

    protected static void verifyServices(ServiceRegistry services, Set<Class<? extends Service>> requiredServices) {
        for (Class<? extends Service> serviceType : requiredServices) {
            if (!services.contains(serviceType)) {
                throw new IllegalStateException(UNSPECIFIED_REQUIRED_SERVICE, serviceType.getName());
            }
        }
    }

    public BeansXml parse(Iterable<URL> urls) {
        return beansXmlParser.parse(urls);
    }

    public BeansXml parse(URL url) {
        return beansXmlParser.parse(url);
    }

    public Iterable<Metadata<Extension>> loadExtensions(ClassLoader classLoader) {
        return ServiceLoader.load(Extension.class, classLoader);
    }

	
}
