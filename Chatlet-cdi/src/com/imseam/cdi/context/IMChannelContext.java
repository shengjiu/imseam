package com.imseam.cdi.context;

import org.jboss.weld.context.AbstractBoundContext;
import org.jboss.weld.context.beanstore.NamingScheme;
import org.jboss.weld.context.beanstore.SimpleNamingScheme;

import com.imseam.cdi.context.beanstore.AttributesBackedBeanStore;
import com.imseam.chatlet.IAttributes;

public class IMChannelContext extends AbstractBoundContext<IAttributes> {
	
	private static final String IDENTIFIER = IMChannelContext.class.getName();
	
	private final NamingScheme namingScheme;

	/**
	 * Constructor
	 */
	public IMChannelContext() {
		super(false);
		this.namingScheme = new SimpleNamingScheme(IMChannelContext.class.getName());
	}
	
	@Override
    public Class<IMChannelScoped> getScope() {
        return IMChannelScoped.class;
    }

	@Override
	public String toString() {
		String active = isActive() ? "Active " : "Inactive ";
		String beanStoreInfo = getBeanStore() == null ? "" : getBeanStore().toString();
		return active + "channel context " + beanStoreInfo;
	}


	
	@Override
    public boolean associate(IAttributes storage) {
        if (getBeanStore() == null) {
            storage.setAttribute(IDENTIFIER, IDENTIFIER);
            setBeanStore(new AttributesBackedBeanStore(storage, namingScheme));
            return true;
        } else {
            return false;
        }
    }

	@Override
    public boolean dissociate(IAttributes storage) {
        if (storage.getAttribute(IDENTIFIER)!= null) {
            try {
                storage.removeAttribute(IDENTIFIER);
                setBeanStore(null);
                return true;
            } finally {
                cleanup();
            }

        } else {
            return false;
        }
    }



}
