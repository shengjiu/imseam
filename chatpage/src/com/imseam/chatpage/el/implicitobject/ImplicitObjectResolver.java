package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

public class ImplicitObjectResolver extends ELResolver
{

    private Map<String, ImplicitObject> implicitObjects;


    public static ELResolver makeResolverForChatpages()
    {
        Map<String, ImplicitObject> implicitObjectMap = new HashMap<String, ImplicitObject>(30);//14
        putImlicitObjectInMap(implicitObjectMap, new ApplicationImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new ApplicationScopeImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new ChannelImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new ChannelScopeImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new BuddyImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new WindowImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new WindowScopeImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new RequestImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new RequestScopeImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new SessionImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new SessionScopeImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new ComponentImplicitObject());
        
        putImlicitObjectInMap(implicitObjectMap, new MessageImplicitObject());
        
        return new ImplicitObjectResolver(implicitObjectMap);
    }

    private static void putImlicitObjectInMap(Map<String, ImplicitObject> implicitObjectMap, ImplicitObject implicitObject){
    	implicitObjectMap.put(implicitObject.getName(), implicitObject);
    }
    private ImplicitObjectResolver()
    {
        super();
        this.implicitObjects = new HashMap<String, ImplicitObject>();
    }

    
    private ImplicitObjectResolver(Map<String, ImplicitObject> implicitObjects)
    {
        this();
        this.implicitObjects = implicitObjects;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException,
        PropertyNotFoundException, PropertyNotWritableException, ELException
    {

        if (base != null)
        {
            return;
        }
        if (property == null)
        {
            throw new PropertyNotFoundException();
        }
        if (!(property instanceof String))
        {
            return;
        }

        String strProperty = property.toString();

        if (implicitObjects.containsKey(strProperty))
        {
            throw new PropertyNotWritableException();
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException,
        PropertyNotFoundException, ELException
    {

        if (base != null)
        {
            return false;
        }
        if (property == null)
        {
            throw new PropertyNotFoundException();
        }
        if (!(property instanceof String))
        {
            return false;
        }

        String strProperty = property.toString();

        if (implicitObjects.containsKey(strProperty))
        {
            context.setPropertyResolved(true);
            return true;
        }

        return false;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) throws NullPointerException,
        PropertyNotFoundException, ELException
    {

        if (base != null)
        {
            return null;
        }
        if (property == null)
        {
            throw new PropertyNotFoundException();
        }
        if (!(property instanceof String))
        {
            return null;
        }

        String strProperty = property.toString();

        ImplicitObject obj = implicitObjects.get(strProperty);
        if (obj != null)
        {
            context.setPropertyResolved(true);
            return obj.getValue(context);
        }

        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) throws NullPointerException,
        PropertyNotFoundException, ELException
    {

        if (base != null)
        {
            return null;
        }
        if (property == null)
        {
            throw new PropertyNotFoundException();
        }
        if (!(property instanceof String))
        {
            return null;
        }

        String strProperty = property.toString();

        if (implicitObjects.containsKey(strProperty))
        {
            context.setPropertyResolved(true);
        }

        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base)
    {
        if (base != null)
        {
            return null;
        }

        ArrayList<FeatureDescriptor> descriptors = new ArrayList<FeatureDescriptor>(implicitObjects.size());

        for (ImplicitObject obj : implicitObjects.values())
        {
            descriptors.add(obj.getDescriptor());
        }

        return descriptors.iterator();
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base)
    {
        if (base != null)
        {
            return null;
        }

        return String.class;
    }

}
