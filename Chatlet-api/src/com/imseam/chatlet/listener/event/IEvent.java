package com.imseam.chatlet.listener.event;

import java.io.Serializable;

import com.imseam.chatlet.IContext;


public interface IEvent extends IContext, Serializable {

    public Object getSource();
}