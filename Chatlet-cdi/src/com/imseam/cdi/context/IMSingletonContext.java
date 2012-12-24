package com.imseam.cdi.context;


import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import org.jboss.weld.context.AbstractSharedContext;
import org.jboss.weld.context.SingletonContext;


public class IMSingletonContext extends AbstractSharedContext implements SingletonContext {

    public Class<? extends Annotation> getScope() {
        return Singleton.class;
    }

}
