package com.imseam.raptor;

import com.imseam.chatlet.exception.IdentifierNotExistingException;

public interface IWindowManager {
	
	void initApplication(IChatletApplication application);
	
	void onWindowCreated(IMessengerWindow window);
	
	void onWindowStopped(IMessengerWindow window);
	
	IMessengerWindow getWindowByUid(String windowUid) throws IdentifierNotExistingException;
	
}
