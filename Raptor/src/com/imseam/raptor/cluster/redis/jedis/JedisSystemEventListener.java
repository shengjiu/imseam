package com.imseam.raptor.cluster.redis.jedis;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.raptor.chatlet.UidHelper;

public class JedisSystemEventListener implements ISystemEventListener {
	
	private static Log log = LogFactory.getLog(JedisSystemEventListener.class);
	

	@Override
	public void initialize(Object source, Map<String, String> params) {

	}

	@Override
	public void onApplicationInitialized(ApplicationEvent event) {
		

	}

	@Override
	public void onApplicationStopped(ApplicationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSessionStarted(SessionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSessionStopped(SessionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBuddyAdded(BuddyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBuddyRemoved(BuddyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBuddySignIn(BuddyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBuddySignOff(BuddyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBuddyStatusChange(BuddyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionStarted(ConnectionEvent event) {
		
		JedisGlobalEvent globalEvent = new ConnectionCreatedEvent(event.getConnection().getUid(), UidHelper.getLocalServerId());
		
		JedisClusterInvocationDistributor.publishGlobalEvent(globalEvent);

	}

	@Override
	public void onConnectionStopped(ConnectionEvent event) {
		
		 JedisGlobalEvent globalEvent = new ConnectionCreatedEvent(event.getConnection().getUid(), UidHelper.getLocalServerId());		
		JedisClusterInvocationDistributor.publishGlobalEvent(globalEvent);
	}

	@Override
	public void onWindowStarted(WindowEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWindowStopped(WindowEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserJoinWindow(UserJoinWindowEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserLeaveWindow(UserJoinWindowEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWindowEventReceived(IWindow window, IEvent event) {
		// TODO Auto-generated method stub
		
	}

}
