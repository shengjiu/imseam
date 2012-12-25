package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;

import javax.el.ELContext;

import com.imseam.chatlet.IApplication;

public class ApplicationImplicitObject extends ImplicitObject
{

    private static final String NAME = "application";

    public ApplicationImplicitObject()
    {
    }

    @Override
    public IApplication getValue(ELContext context){
        return chatpageContext(context).getApplication();
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
        return makeDescriptor(NAME, "Represents the application environment", Object.class);
    }
}
