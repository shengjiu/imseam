package com.imseam.connector.generic;

import java.util.Map;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.exception.StartActiveWindowException;

public interface IMessengerConnectionAdapter {
	
	void init(Map<String, String> params);
	
	boolean connect();
	
	void disconnecting();
	
	boolean isConnected();
	
	BuddyStatusEnum getBuddyStatus(String messengerID);
	
	void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException;
	
}
