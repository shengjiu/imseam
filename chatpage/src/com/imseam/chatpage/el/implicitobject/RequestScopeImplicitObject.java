package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.Map;

import javax.el.ELContext;

public class RequestScopeImplicitObject extends ImplicitObject
{

    private static final String NAME = "requestScope";

    public RequestScopeImplicitObject()
    {
    }

    @Override
    public Map<String, Object> getValue(ELContext context)
    {
        return this.getMapFromIAttributes(this.chatpageContext(context).getRequest());
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
        return makeDescriptor(NAME, "Request scope attributes", Map.class);
    }

}
