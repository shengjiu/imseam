package com.imseam.raptor;

import com.imseam.chatlet.IBuddy;
import com.imseam.chatlet.ISession;

public interface ISessionManager {
	
	void initApplication(IChatletApplication application);
	
	ISession findUserSession(String buddyUid);
	
	ISession createUserSession(IBuddy buddy);
	
}
