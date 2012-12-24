package com.imseam.cdi.chatlet.spi;

import java.io.Serializable;

import com.imseam.cdi.chatlet.Id;
import com.imseam.chatlet.IContext;


public abstract class Func implements Serializable{
	

	private static final long serialVersionUID = -1949359083386517643L;
	
	private Id sourceId;

	
	public Func(Id sourceId){
		this.sourceId = sourceId;

	}
	
	abstract public void invoke(IContext context);

	public Id getSourceId() {
		return sourceId;
	}
	
	
}
