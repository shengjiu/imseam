package com.imseam.chatlet.listener;

import com.imseam.chatlet.IInitable;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;

public interface ISystemEventListener extends IInitable {

	void onApplicationInitialized(ApplicationEvent event);

	void onApplicationStopped(ApplicationEvent event);
	
	void onSessionStarted(SessionEvent event);
	
	void onSessionStopped(SessionEvent event);

	void onBuddyAdded(BuddyEvent event);

	void onBuddyRemoved(BuddyEvent event);

	void onBuddySignIn(BuddyEvent event);

	void onBuddySignOff(BuddyEvent event);

	void onBuddyStatusChange(BuddyEvent event);

	void onConnectionStarted(ConnectionEvent event);

	void onConnectionStopped(ConnectionEvent event);

	void onWindowStarted(WindowEvent event);

	void onWindowStopped(WindowEvent event);

	void onUserJoinWindow(UserJoinWindowEvent event);

	void onUserLeaveWindow(UserJoinWindowEvent event);
	
}
