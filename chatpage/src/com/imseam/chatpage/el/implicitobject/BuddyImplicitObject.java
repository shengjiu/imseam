package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;
import java.util.Map;

import javax.el.ELContext;

import com.imseam.chatlet.IBuddy;
public class BuddyImplicitObject extends ImplicitObject
{

    private static final String NAME = "buddy";

    
    public BuddyImplicitObject()
    {
    }

    @Override
    public IBuddy getValue(ELContext context)
    {
        return this.chatpageContext(context).getChannel().getBuddy();
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
