package com.imseam.cdi.context;


import javax.enterprise.context.ApplicationScoped;

import org.jboss.weld.context.AbstractSharedContext;
import org.jboss.weld.context.ApplicationContext;


public class IMApplicationContext extends AbstractSharedContext implements ApplicationContext {

    public Class<ApplicationScoped> getScope() {
        return ApplicationScoped.class;
    }

}

