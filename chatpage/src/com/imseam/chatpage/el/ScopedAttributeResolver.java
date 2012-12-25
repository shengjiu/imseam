package com.imseam.chatpage.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatpage.context.ChatpageContext;

public final class ScopedAttributeResolver extends ELResolver
{

    public ScopedAttributeResolver()
    {
    }

    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object value)
        throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException
    {
        if (base != null)
        {
            return;
        }

        if (property == null)
        {
            throw new PropertyNotFoundException();
        }

        final IAttributes scopedMap = findScopedMap(chatpageContext(context), property);
        if (scopedMap != null)
        {
            scopedMap.setAttribute((String)property, value);
        }
        else
        {
            chatpageContext(context).getRequest().setAttribute((String)property, value);
        }

        context.setPropertyResolved(true);
    }

    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property)
        throws NullPointerException, PropertyNotFoundException, ELException
    {
        if (base == null)
        {
            context.setPropertyResolved(true);
        }

        return false;
    }

    @Override
    public Object getValue(final ELContext context, final Object base, final Object property)
        throws NullPointerException, PropertyNotFoundException, ELException
    {
        if (base != null)
        {
            return null;
        }

        if (property == null)
        {
            throw new PropertyNotFoundException();
        }

        context.setPropertyResolved(true);

        final IAttributes scopedMap = findScopedMap(chatpageContext(context), property);
        if (scopedMap != null)
        {
            return scopedMap.getAttribute((String)property);
        }

        return null;
    }

    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property)
        throws NullPointerException, PropertyNotFoundException, ELException
    {

        if (base != null)
        {
            return null;
        }
        if (property == null)
        {
            throw new PropertyNotFoundException();
        }

        context.setPropertyResolved(true);
        return Object.class;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base)
    {

        if (base != null)
        {
            return null;
        }

        final List<FeatureDescriptor> descriptorList = new ArrayList<FeatureDescriptor>();
        ChatpageContext chatpageContext = chatpageContext(context);
        addDescriptorsToList(descriptorList, chatpageContext.getRequest());
        addDescriptorsToList(descriptorList, chatpageContext.getSession());
        addDescriptorsToList(descriptorList, chatpageContext.getApplication());

        return descriptorList.iterator();
    }

    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base)
    {

        if (base != null)
        {
            return null;
        }

        return String.class;
    }

    // side effect: modifies the list
    private static void addDescriptorsToList(final List<FeatureDescriptor> descriptorList,
                                             final IAttributes scopeMap)
    {
        for (String name : scopeMap.getAttributeNames())
        {
            if(scopeMap.getAttribute(name) != null){
	            Class<?> runtimeType = scopeMap.getAttribute(name).getClass();
	            descriptorList.add(makeDescriptor(name, runtimeType));
            }
        }
    }

    private static FeatureDescriptor makeDescriptor(final String name, final Class<?> runtimeType)
    {
        FeatureDescriptor fd = new FeatureDescriptor();
        fd.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
        fd.setValue(ELResolver.TYPE, runtimeType);
        fd.setName(name);
        fd.setDisplayName(name);
        fd.setShortDescription(name);
        fd.setExpert(false);
        fd.setHidden(false);
        fd.setPreferred(true);
        return fd;
    }

    // returns null if not found
    private static IAttributes findScopedMap(final ChatpageContext chatpageContext, final Object property){
        if (chatpageContext == null){
            return null;
        }
        
        IAttributes attributes = null;
        
        attributes = chatpageContext.getRequest(); 
        if(attributes != null && attributes.getAttributeNames().contains(property)){
        	return attributes;
        }

        attributes = chatpageContext.getChannel(); 
        if(attributes != null && attributes.getAttributeNames().contains(property)){
        	return attributes;
        }

        attributes = chatpageContext.getWindow(); 
        if(attributes != null && attributes.getAttributeNames().contains(property)){
        	return attributes;
        }

        attributes = chatpageContext.getSession(); 
        if(attributes != null && attributes.getAttributeNames().contains(property)){
        	return attributes;
        }

        attributes = chatpageContext.getConnection(); 
        if(attributes != null && attributes.getAttributeNames().contains(property)){
        	return attributes;
        }

        attributes = chatpageContext.getApplication(); 
        if(attributes != null && attributes.getAttributeNames().contains(property)){
        	return attributes;
        }

        // not found
        return null;
    }

    // get the FacesContext from the ELContext
    private static ChatpageContext chatpageContext(final ELContext context)
    {
        return (ChatpageContext)context.getContext(ChatpageContext.class);
    }
    
}
