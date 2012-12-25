package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;

import javax.el.ELContext;

import com.imseam.chatlet.ISession;

public class SessionImplicitObject extends ImplicitObject
{

    private static final String NAME = "session";

    
    public SessionImplicitObject()
    {
    }

    @Override
    public ISession getValue(ELContext context){
        return chatpageContext(context).getSession();
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
        return makeDescriptor(NAME, "Represents the session environment", Object.class);
    }
}
