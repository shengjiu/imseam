package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;

import javax.el.ELContext;

import com.imseam.chatlet.IAttributes;

public class RequestImplicitObject extends ImplicitObject
{

    private static final String NAME = "request";

    
    public RequestImplicitObject()
    {
    }

    @Override
    public IAttributes getValue(ELContext context){
        return chatpageContext(context).getRequest();
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
        return makeDescriptor(NAME, "Represents the request environment", Object.class);
    }
}
