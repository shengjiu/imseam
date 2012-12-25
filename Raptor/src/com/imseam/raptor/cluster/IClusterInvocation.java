package com.imseam.raptor.cluster;

import java.io.Serializable;
import java.util.Date;

import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.raptor.IChatletApplication;


public interface IClusterInvocation<T extends IContext> extends Serializable{
	
	void invoke(IChatletApplication application, T context, IEventErrorCallback handler);
	
	Date getTimestamp();
	
}
