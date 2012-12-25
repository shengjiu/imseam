package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.Map;

import javax.el.ELContext;

public class WindowScopeImplicitObject extends ImplicitObject
{

    private static final String NAME = "windowScope";

    public WindowScopeImplicitObject()
    {
    }

    @Override
    public Map<String, Object> getValue(ELContext context)
    {
        return this.getMapFromIAttributes(this.chatpageContext(context).getWindow());
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
        return makeDescriptor(NAME, "Window scope attributes", Map.class);
    }

}
