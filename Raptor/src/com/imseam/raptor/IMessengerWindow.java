package com.imseam.raptor;

import java.util.Locale;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IMessageSender;
import com.imseam.raptor.chatlet.ChannelContext;
import com.imseam.raptor.chatlet.WindowContext;


public interface IMessengerWindow {
	
	WindowContext getWindowContext();

	IMessengerConnection getConnection();
	
	Locale getLocale();
	
	void closeWindow();
	
	void inviteMessengerUser(String userName);
	
	void kickoutMessengerUser(String userName);
	
	void sendResponse(IChatletMessage... response);
	
	void userJoin(String ... users);
	
	void userLeave(String ... users);
	
	void requestReceived(IChatletMessage message, String userName);
	
	ChannelContext[] getOnboardChannels();
	
	ChannelContext getDefaultChannel();
	
	IMessageSender getMessageSender();
	
	
}
