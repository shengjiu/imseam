package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.Map;

import javax.el.ELContext;
public class ChannelScopeImplicitObject extends ImplicitObject
{

    private static final String NAME = "channelScope";

    
    public ChannelScopeImplicitObject()
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
        return makeDescriptor(NAME, "Channel scope attributes", Map.class);
    }

}
