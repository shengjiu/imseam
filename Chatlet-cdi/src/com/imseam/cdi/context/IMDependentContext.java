package com.imseam.cdi.context;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.Interceptor;

import org.jboss.weld.bean.ManagedBean;
import org.jboss.weld.bean.ProducerField;
import org.jboss.weld.bean.ProducerMethod;
import org.jboss.weld.context.DependentContext;
import org.jboss.weld.context.SerializableContextualInstanceImpl;
import org.jboss.weld.context.WeldCreationalContext;
import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.serialization.spi.ContextualStore;

public class IMDependentContext implements DependentContext {

    private final ContextualStore contextualStore;

    public IMDependentContext(ContextualStore contextualStore) {
        this.contextualStore = contextualStore;
    }

    /**
     * Overridden method always creating a new instance
     *
     * @param contextual The bean to create
     * @param create     Should a new one be created
     */
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        if (!isActive()) {
            throw new ContextNotActiveException();
        }
        if (creationalContext != null) {
            T instance = contextual.create(creationalContext);
            if (creationalContext instanceof WeldCreationalContext<?>) {
                addDependentInstance(instance, contextual, (WeldCreationalContext<T>) creationalContext);
            }
            return instance;
        } else {
            return null;
        }
    }

    protected <T> void addDependentInstance(T instance, Contextual<T> contextual, WeldCreationalContext<T> creationalContext) {
        // by this we are making sure that the dependent instance has no transitive dependency with @PreDestroy / disposal method
        if (creationalContext.getDependentInstances().isEmpty()) {
            if (contextual instanceof ManagedBean<?> && ! isInterceptorOrDecorator(contextual)) {
                ManagedBean<?> bean = (ManagedBean<?>) contextual;
                if (bean.getPreDestroy().isEmpty() && !bean.hasInterceptors() && bean.hasDefaultProducer()) {
                    // there is no @PreDestroy callback to call when destroying this dependent instance
                    // therefore, we do not need to keep the reference
                    return;
                }
            }
            if (contextual instanceof ProducerMethod<?, ?>) {
                ProducerMethod<?, ?> method = (ProducerMethod<?, ?>) contextual;
                if (method.getDisposalMethod() == null && method.hasDefaultProducer()) {
                    // there is no disposal method to call when destroying this dependent instance
                    // therefore, we do not need to keep the reference
                    return;
                }
            }
            if (contextual instanceof ProducerField<?, ?>) {
                ProducerField<?, ?> field = (ProducerField<?, ?>) contextual;
                if (field.hasDefaultProducer()) {
                    return;
                }
            }
        }

        // Only add the dependent instance if none of the conditions above is met
        ContextualInstance<T> beanInstance = new SerializableContextualInstanceImpl<Contextual<T>, T>(contextual, instance, creationalContext, contextualStore);
        creationalContext.addDependentInstance(beanInstance);
    }

    private boolean isInterceptorOrDecorator(Contextual<?> contextual) {
        return contextual instanceof Interceptor<?> || contextual instanceof Decorator<?>;
    }

    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    public boolean isActive() {
        return true;
    }

    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

}