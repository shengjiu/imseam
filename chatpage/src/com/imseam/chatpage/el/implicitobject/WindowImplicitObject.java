package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;

import javax.el.ELContext;

import com.imseam.chatlet.IWindow;

public class WindowImplicitObject extends ImplicitObject
{

    private static final String NAME = "window";

    
    public WindowImplicitObject()
    {
    }

    @Override
    public IWindow getValue(ELContext context){
        return chatpageContext(context).getWindow();
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
        return makeDescriptor(NAME, "Represents the window environment", Object.class);
    }
}
