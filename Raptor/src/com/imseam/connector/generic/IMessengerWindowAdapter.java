package com.imseam.connector.generic;

import com.imseam.chatlet.IChatletMessage;

public interface IMessengerWindowAdapter {
	
	public String[] getMessengerUserUIDs();
	
	public String getUserName(String uid);

	public String getId();
	
	void sendResponse(IChatletMessage... response);
	
	public void close();
	
}

