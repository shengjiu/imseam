package com.imseam.cdi.context;

import java.lang.annotation.Annotation;

import org.jboss.weld.context.AbstractBoundContext;
import org.jboss.weld.context.ManagedContext;
import org.jboss.weld.context.beanstore.NamingScheme;
import org.jboss.weld.context.beanstore.SimpleNamingScheme;
import org.jboss.weld.context.cache.RequestScopedBeanCache;

import com.imseam.cdi.context.beanstore.IMRequestBeanStore;
import com.imseam.chatlet.IAttributes;

public class IMRequestContext extends AbstractBoundContext<IAttributes> implements ManagedContext{

    private static final String IDENTIFIER = IMRequestContext.class.getName();

    private final NamingScheme namingScheme;

    /**
     * Constructor
     */
    public IMRequestContext() {
        super(false);
        this.namingScheme = new SimpleNamingScheme(IMRequestContext.class.getName());
    }

    public boolean associate(IAttributes request) {
        if (request.getAttribute(IDENTIFIER) == null) {
            request.setAttribute(IDENTIFIER, IDENTIFIER);
            setBeanStore(new IMRequestBeanStore(request, namingScheme));
            getBeanStore().attach();
            return true;
        } else {
            return false;
        }
    }

    public boolean dissociate(IAttributes request) {
        if (request.getAttribute(IDENTIFIER) != null) {
            try {
                setBeanStore(null);
                request.removeAttribute(IDENTIFIER);
                return true;
            } finally {
                cleanup();
            }
        } else {
            return false;
        }

    }

    @Override
    public void activate() {
        super.activate();
        RequestScopedBeanCache.beginRequest();
    }

    @Override
    public void deactivate() {
        try {
            RequestScopedBeanCache.endRequest();
        } finally {
            super.deactivate();
        }
    }

    public Class<? extends Annotation> getScope() {
        return IMRequestScoped.class;
    }

}
