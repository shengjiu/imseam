package com.imseam.chatpage.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.jboss.el.lang.FunctionMapperImpl;
import org.jboss.el.lang.VariableMapperImpl;

import com.imseam.chatpage.context.ChatpageContext;

public class ChatpageELContext extends ELContext
{

    private ELResolver _elResolver;
    private FunctionMapper _functionMapper;
    private VariableMapper _variableMapper;

    public ChatpageELContext(ELResolver elResolver, ChatpageContext facesContext)
    {
        this._elResolver = elResolver;
        putContext(ChatpageContext.class, facesContext);

        // TODO: decide if we need to implement our own FunctionMapperImpl and
        // VariableMapperImpl instead of relying on Tomcat's version.
         this._functionMapper = new FunctionMapperImpl();
         this._variableMapper = new VariableMapperImpl();
    }

    @Override
    public VariableMapper getVariableMapper()
    {
        return _variableMapper;
    }

    public void setVariableMapper(VariableMapper varMapper)
    {
        _variableMapper = varMapper;
    }

    @Override
    public FunctionMapper getFunctionMapper()
    {
        return _functionMapper;
    }

    public void setFunctionMapper(FunctionMapper functionMapper)
    {
        _functionMapper = functionMapper;
    }

    @Override
    public ELResolver getELResolver()
    {
        return _elResolver;
    }

}
