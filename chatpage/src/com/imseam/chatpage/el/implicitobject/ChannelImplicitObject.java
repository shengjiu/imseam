package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;

import javax.el.ELContext;

import com.imseam.chatlet.IChannel;

public class ChannelImplicitObject extends ImplicitObject
{

    private static final String NAME = "channel";

    
    public ChannelImplicitObject()
    {
    }

    @Override
    public IChannel getValue(ELContext context){
        return chatpageContext(context).getChannel();
    }

    @Override
    public String getName(){
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
        return makeDescriptor(NAME, "Represents the channel environment", Object.class);
    }
}
