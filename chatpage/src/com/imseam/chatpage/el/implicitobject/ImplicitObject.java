package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatpage.context.ChatpageContext;


public abstract class ImplicitObject
{

    public abstract Object getValue(ELContext context);

    public abstract FeatureDescriptor getDescriptor();

    /**
     * Returns an interned String representing the name of the implicit object.
     */
    public abstract String getName();

    /**
     * Returns the most general type allowed for a future call to setValue()
     */
    public abstract Class<?> getType();

    protected FeatureDescriptor makeDescriptor(String name, String description, Class<?> elResolverType)
    {
        FeatureDescriptor fd = new FeatureDescriptor();
        fd.setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
        fd.setValue(ELResolver.TYPE, elResolverType);
        fd.setName(name);
        fd.setDisplayName(name);
        fd.setShortDescription(description);
        fd.setExpert(false);
        fd.setHidden(false);
        fd.setPreferred(true);
        return fd;
    }

    // get the chatpageContext from the ELContext
    protected ChatpageContext chatpageContext(ELContext context)
    {
        return (ChatpageContext) context.getContext(ChatpageContext.class);
    }
    
    
    protected Map<String, Object> getMapFromIAttributes(IAttributes attributes){
    	if(attributes == null) return null;
    	
    	Map<String, Object> attributeMap =  new HashMap<String, Object>();
    	
    	for (String attributeName : attributes.getAttributeNames()){
			attributeMap.put(attributeName, attributes.getAttribute(attributeName));
		}
		return attributeMap;
    	
    }
    
    

}
