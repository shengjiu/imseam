package com.imseam.cdi.context.beanstore;

import static org.jboss.weld.logging.Category.CONTEXT;
import static org.jboss.weld.logging.LoggerFactory.loggerFactory;
import static org.jboss.weld.logging.messages.ContextMessage.CONTEXTUAL_INSTANCE_ADDED;
import static org.jboss.weld.logging.messages.ContextMessage.CONTEXTUAL_INSTANCE_FOUND;
import static org.jboss.weld.logging.messages.ContextMessage.CONTEXTUAL_INSTANCE_REMOVED;
import static org.jboss.weld.logging.messages.ContextMessage.CONTEXT_CLEARED;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.context.beanstore.BeanStore;
import org.jboss.weld.context.beanstore.BoundBeanStore;
import org.jboss.weld.context.beanstore.HashMapBeanStore;
import org.jboss.weld.context.beanstore.LockStore;
import org.jboss.weld.context.beanstore.LockedBean;
import org.jboss.weld.context.beanstore.NamingScheme;
import org.jboss.weld.util.collections.EnumerationList;
import org.jboss.weld.util.reflection.Reflections;
import org.slf4j.cal10n.LocLogger;

import com.imseam.chatlet.IAttributes;

public class AttributesBackedBeanStore implements BoundBeanStore { 


    private static final LocLogger log = loggerFactory().getLogger(CONTEXT);

    private final HashMapBeanStore beanStore;
    private final NamingScheme namingScheme;

    
	private IAttributes attributes;
	
	private boolean attached;
	
    private static final String BEANSTORE_LOCK_KEY = "org.jboss.weld.context.beanstore.LockStore";
    
	private transient volatile LockStore lockStore;
	
    public AttributesBackedBeanStore(IAttributes attributes, NamingScheme namingScheme) {
        this.namingScheme = namingScheme;
        this.beanStore = new HashMapBeanStore();
        this.attributes = attributes;
    }

  

    public <T> ContextualInstance<T> get(String id) {
        ContextualInstance<T> instance = beanStore.get(id);
        log.trace(CONTEXTUAL_INSTANCE_FOUND, id, instance, this);
        return instance;
    }

    public <T> void put(String id, ContextualInstance<T> instance) {
        beanStore.put(id, instance); // moved due to WELD-892
        if (isAttached()) {
            String prefixedId = namingScheme.prefix(id);
            setAttribute(prefixedId, instance);
        }
        log.trace(CONTEXTUAL_INSTANCE_ADDED, instance.getContextual(), id, this);
    }

    public void clear() {
        Iterator<String> it = iterator();
        while (it.hasNext()) {
            String id = it.next();
            if (isAttached()) {
                String prefixedId = namingScheme.prefix(id);
                removeAttribute(prefixedId);
            }
            it.remove();
            log.trace(CONTEXTUAL_INSTANCE_REMOVED, id, this);
        }
        log.trace(CONTEXT_CLEARED, this);
    }

    public boolean contains(String id) {
        return get(id) != null;
    }

    protected NamingScheme getNamingScheme() {
        return namingScheme;
    }

    public Iterator<String> iterator() {
        return beanStore.iterator();
    }


    /**
     * Gets an enumeration of the attribute names present in the underlying
     * storage
     *
     * @return The attribute names
     */
    protected Collection<String> getPrefixedAttributeNames() {
        return getNamingScheme().filterIds(getAttributeNames());
    }


    @Override
    public LockedBean lock(final String id) {
        LockStore lockStore = getLockStore();
        if(lockStore == null) {
            //if the lockstore is null then no locking is necessary, as the underlying
            //context is single threaded
            return null;
        }
        return lockStore.lock(id);
    }

    
    protected Object getAttribute(String key) {
        return attributes.getAttribute(key);
    }

    
    protected void removeAttribute(String key) {
    	attributes.removeAttribute(key);
    }

    
    protected Collection<String> getAttributeNames() {
        return attributes.getAttributeNames();
    }

    
    protected void setAttribute(String key, Object instance) {
    	attributes.setAttribute(key, instance);
    }



    /**
     * Detach the bean store, causing updates to longer be written through to the
     * underlying store.
     */
    @Override
    public boolean detach() {
        if (attached) {
            attached = false;
            log.trace("Bean store " + this + " is detached");
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * Attach the bean store, any updates from now on will be written through to
     * the underlying store.
     * </p>
     * <p/>
     * <p>
     * When the bean store is attached, the detached state is assumed to be
     * authoritative if there are any conflicts.
     * </p>
     */
    @Override
    public boolean attach() {
        if (!attached) {
            attached = true;
            // beanStore is authoritative, so copy everything to the backing store
            for (String id : beanStore) {
                ContextualInstance<?> instance = beanStore.get(id);
                String prefixedId = getNamingScheme().prefix(id);
                log.trace("Updating underlying store with contextual " + instance + " under ID " + id);
                setAttribute(prefixedId, instance);
            }

            /*
            * Additionally copy anything not in the bean store but in the session
            * into the bean store
            */
            for (String prefixedId : getPrefixedAttributeNames()) {
                String id = getNamingScheme().deprefix(prefixedId);
                if (!beanStore.contains(id)) {
                    ContextualInstance<?> instance = (ContextualInstance<?>) getAttribute(prefixedId);
                    beanStore.put(id, instance);
                    log.trace("Adding detached contextual " + instance + " under ID " + id);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAttached() {
        return attached;
    }

    
    public LockStore getLockStore() {
        LockStore lockStore = this.lockStore;
        if (lockStore == null) {
            lockStore = (LockStore) attributes.getAttribute(BEANSTORE_LOCK_KEY);
            if (lockStore == null) {
                //we don't really have anything we can lock on
                //so we just acquire a big global lock
                //this should only be taken on session creation though
                //so should not be a problem
                synchronized (attributes) {
                    lockStore = (LockStore) attributes.getAttribute(BEANSTORE_LOCK_KEY);
                    if (lockStore == null) {
                        lockStore = new LockStore();
                        attributes.setAttribute(BEANSTORE_LOCK_KEY, lockStore);
                    }
                }
            }
            this.lockStore = lockStore;
        }
        return lockStore;

    }
}
