package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.Map;

import javax.el.ELContext;

public class SessionScopeImplicitObject extends ImplicitObject
{

    private static final String NAME = "sessionScope";

    
    public SessionScopeImplicitObject()
    {
    }

    @Override
    public Map<String, Object> getValue(ELContext context)
    {
        return this.getMapFromIAttributes(this.chatpageContext(context).getSession());
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Class<?> getType()
    {
        return null;
    }

    @Override
    public FeatureDescriptor getDescriptor()
    {
        return makeDescriptor(NAME, "Session scope attributes", Map.class);
    }

}
