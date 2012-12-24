package com.imseam.chatlet;

import java.util.Collection;
import java.util.Set;

import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;


public interface IConnection extends IContext{
	
	String getHostUserId();
	
	String getConnectionName();
	
	boolean isConnected();
	
	String getServiceId();
	
	int getOpenWindowLimit();
	
	Collection<? extends IBuddy> getAllBuddies();
	
	Collection<? extends IBuddy> getOnlineBuddies();
	
	IApplication getApplication();
	
	IBuddy getBuddyByUserId(String userId);

	IBuddy getBuddyByBuddyUid(String buddyUid);
	
	void inviteBuddy(String userId) throws InviteBuddyException;
	
	Set<IWindow> getBuddyActiveWindowSet(String buddyUid);
	
	void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException;
	
}
