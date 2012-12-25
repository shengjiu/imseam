package com.imseam.raptor.cluster;

import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;

public interface IClusterInvocationDistributor{
	
	void initApplication(IChatletApplication application);
	
	void distributeRequest(IEventErrorCallback handler, IClusterInvocation<? extends IContext> request, UidType idType, String targetUid) throws IdentifierNotExistingException;
	
	void distributeConnectionRequest(IEventErrorCallback handler, IClusterInvocation<IConnection> request, String ...connectionUids) throws IdentifierNotExistingException;
	
	void distributeWindowRequest(IEventErrorCallback handler, IClusterInvocation<IWindow> request, String...windowUids) throws IdentifierNotExistingException;
	
}
