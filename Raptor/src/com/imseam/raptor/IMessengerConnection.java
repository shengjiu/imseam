package com.imseam.raptor;

import java.util.Collection;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.raptor.chatlet.ConnectionContext;
import com.imseam.raptor.chatlet.MessengerBuddy;
import com.imseam.raptor.chatlet.UserSession;

public interface IMessengerConnection{
	
	void initialize(); 

	ConnectionContext getConnectionContext();
	
	IChatletApplication getApplication();
	
	boolean connect(); 

	boolean reConnect();

	void disconnect();
	
	boolean ping();
	
	void fireBuddyAdded(String userName);
	
	void fireBuddyRemoved(String userName);
	
	void fireBuddySignIn(String userName);

	void fireBuddySignOff(String userName);
	
	void fireBuddyStatusChange(String userName);
	
	void fireWindowStarted(IMessengerWindow window);
	
	void fireWindowStopped(IMessengerWindow window);
	
	void internalWindowStopped(IMessengerWindow window);
	
	String getHostUserId();
	
	String getPassword();
	
	String getConnectionName();
	
	boolean isConnected();
	
	String getServiceId();
	
	BuddyStatusEnum getBuddyStatus(String messengerID);
	
	Collection<MessengerBuddy> getAllBuddies();
	
	Collection<MessengerBuddy> getOnlineBuddies();
	
	MessengerBuddy getBuddyByUserId(String userId);

	MessengerBuddy getBuddyByBuddyUid(String buddyUid);
	
	void inviteBuddy(String userId) throws InviteBuddyException;
	
	boolean isInviteBuddySupported();
	
	void sessionStopped(UserSession oldSession);
	
	void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException;
	
	int getWindowLimit();
	
	String getUid();

}
