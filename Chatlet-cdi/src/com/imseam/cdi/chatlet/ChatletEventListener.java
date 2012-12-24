package com.imseam.cdi.chatlet;

import java.util.Map;

import com.imseam.chatlet.IChatlet;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.BuddyNotAvailableForChatException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.chatlet.listener.IMeetingEventListener;
import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;

public class ChatletEventListener implements IMeetingEventListener, ISystemEventListener, IChatlet {

	public ChatletEventListener() {
	}


	@Override
	public void initialize(Object source, Map<String, String> params) {
		ChatletEventListenerAdaptor.instance().initialize(source, params);
	}
	

	@Override
	public void onApplicationInitialized(ApplicationEvent appEvent) {
		ChatletEventListenerAdaptor.instance().onApplicationInitialized(appEvent);
	}

	@Override
	public void onApplicationStopped(ApplicationEvent appEvent) {
		ChatletEventListenerAdaptor.instance().onApplicationStopped(appEvent);
	}
	
	@Override
	public void onBuddyAdded(BuddyEvent event) {
		ChatletEventListenerAdaptor.instance().onBuddyAdded(event);
	}

	@Override
	public void onBuddyRemoved(BuddyEvent event) {
		ChatletEventListenerAdaptor.instance().onBuddyRemoved(event);
	}

	@Override
	public void onBuddySignIn(BuddyEvent event) {
		ChatletEventListenerAdaptor.instance().onBuddySignIn(event);
	}

	@Override
	public void onBuddySignOff(BuddyEvent event) {
		ChatletEventListenerAdaptor.instance().onBuddySignOff(event);
	}

	@Override
	public void onBuddyStatusChange(BuddyEvent event) {
		ChatletEventListenerAdaptor.instance().onBuddyStatusChange(event);
	}

	@Override
	public void onConnectionStarted(ConnectionEvent event) {
		ChatletEventListenerAdaptor.instance().onConnectionStarted(event);
	}

	@Override
	public void onConnectionStopped(ConnectionEvent event) {
		ChatletEventListenerAdaptor.instance().onConnectionStopped(event);
	}

	@Override
	public void onWindowStarted(WindowEvent event) {
		ChatletEventListenerAdaptor.instance().onWindowStarted(event);
	}

	@Override
	public void onWindowStopped(WindowEvent event) {
		ChatletEventListenerAdaptor.instance().onWindowStopped(event);
	}

	@Override
	public void onUserJoinWindow(UserJoinWindowEvent event) {
		ChatletEventListenerAdaptor.instance().onUserJoinWindow(event);
	}

	@Override
	public void onUserLeaveWindow(UserJoinWindowEvent event) {
		ChatletEventListenerAdaptor.instance().onUserLeaveWindow(event);
	}

	@Override
	public void onSessionStarted(SessionEvent event) {
		ChatletEventListenerAdaptor.instance().onSessionStarted(event);
	}

	@Override
	public void onSessionStopped(SessionEvent event) {
		ChatletEventListenerAdaptor.instance().onSessionStopped(event);
	}
	
	@Override
	public void serviceUserRequest(IUserRequest req, IMessageSender responseSender) {
		ChatletEventListenerAdaptor.instance().serviceUserRequest(req, responseSender);
	}

	@Override
	public void onKickedoutFromMeeting(IWindow window, String sourceWindowUid, String meetingUid) {
		ChatletEventListenerAdaptor.instance().onKickedoutFromMeeting(window, sourceWindowUid, meetingUid);
	}

	@Override
	public void onMeetingStopped(IWindow window, String sourceWindowUid) {
		ChatletEventListenerAdaptor.instance().onMeetingStopped(window, sourceWindowUid);
	}
	

	
	@Override
	public void onEventReceived(IWindow window, IEvent event) {
		ChatletEventListenerAdaptor.instance().onEventReceived(window, event);
	}

	@Override
	public void onOtherWindowLeftMeeting(IWindow window, String sourceWindowId, String kickoutWindowUid) {
		ChatletEventListenerAdaptor.instance().onOtherWindowLeftMeeting(window, sourceWindowId, kickoutWindowUid);
	}

	@Override
	public void onOtherWindowJoinedMeeting(IWindow window, String sourceWindowId, String newWindowUid) {
		ChatletEventListenerAdaptor.instance().onOtherWindowJoinedMeeting(window, sourceWindowId, newWindowUid);
	}

	@Override
	public void onJoinedMeeting(IWindow window, String sourceWindowUid) {
		ChatletEventListenerAdaptor.instance().onJoinedMeeting(window, sourceWindowUid);
	}
	
	@Override
	public boolean beforeInviteWindow(IWindow window) {
		return ChatletEventListenerAdaptor.instance().beforeInviteWindow(window);
	}

	@Override
	public boolean beforeStartActiveWindow(IConnection connection, String buddyUid) throws BuddyNotAvailableForChatException, StartActiveWindowException {
		return ChatletEventListenerAdaptor.instance().beforeStartActiveWindow(connection, buddyUid);
	}

}
