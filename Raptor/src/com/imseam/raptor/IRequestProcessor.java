package com.imseam.raptor;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IInitable;

public interface IRequestProcessor extends IInitable {
	
	void initApplication(IChatletApplication application);
	
	void requestReceived(IChatletMessage message, String username, IMessengerWindow window);

}
