package com.imseam.chatlet.listener.event;

import java.io.Serializable;
import java.util.Date;

import com.imseam.chatlet.IContext;


public interface IEvent extends IContext, Serializable {

    public Object getSource();
    
    Date getTimestamp();
}