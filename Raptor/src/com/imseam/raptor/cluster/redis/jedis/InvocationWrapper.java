package com.imseam.raptor.cluster.redis.jedis;

import java.io.Serializable;
import java.util.List;

import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.raptor.cluster.IClusterInvocation;

public class InvocationWrapper implements Serializable{
	
	private static final long serialVersionUID = -4497502271760223212L;
	private IEventErrorCallback handler;
	private IClusterInvocation<? extends IContext> invocation;
	private UidType type;
	private List<String> targetList;
	
	public InvocationWrapper (IEventErrorCallback handler, IClusterInvocation<? extends IContext> invocation, UidType type, List<String> targetList){
		this.handler = handler;
		this.invocation = invocation;
		this.type = type;
		this.targetList = targetList;
		
	}

	public IEventErrorCallback getHandler() {
		return handler;
	}


	public void setHandler(IEventErrorCallback handler) {
		this.handler = handler;
	}


	public IClusterInvocation<? extends IContext> getInvocation() {
		return invocation;
	}


	public void setInvocation(IClusterInvocation<? extends IContext> invocation) {
		this.invocation = invocation;
	}


	public UidType getType() {
		return type;
	}


	public void setType(UidType type) {
		this.type = type;
	}


	public List<String> getTargetList() {
		return targetList;
	}


	public void setTargetList(List<String> targetList) {
		this.targetList = targetList;
	}
}
