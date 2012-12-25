package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.Map;

import javax.el.ELContext;

public class ConnectionScopeImplicitObject extends ImplicitObject
{

    private static final String NAME = "connectionScope";

    public ConnectionScopeImplicitObject()
    {
    }

    @Override
    public Map<String, Object> getValue(ELContext context)
    {
        return this.getMapFromIAttributes(this.chatpageContext(context).getApplication());
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
        return makeDescriptor(NAME, "Connection scope attributes", Map.class);
    }

}
