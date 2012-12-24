package com.imseam.chatlet;

import java.util.Collection;
import java.util.Locale;

import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.exception.NoMeetingException;
import com.imseam.chatlet.exception.WindowInOtherMeetingException;
import com.imseam.chatlet.listener.event.IEvent;

public interface IWindow extends IContext{
	
	IConnection getConnection();
	
	IChannel getChannelByBuddyUid(String buddyUid);
	
	Collection<? extends IChannel> getOnboardChannels();
	
	IChannel getDefaultChannel();

	IMeeting getMeeting();
	
	IMeeting startMeetingWithBuddy(IEventErrorCallback handler, String... buddyUids) throws WindowInOtherMeetingException, IdentifierNotExistingException;
	
	IMeeting startMeetingWithWindow(IEventErrorCallback handler, String... windowUids) throws WindowInOtherMeetingException, IdentifierNotExistingException;
	
	void leaveMeeting(IEventErrorCallback handler) throws NoMeetingException;
	
	void addBuddyToMeeting(IEventErrorCallback handler, String... buddyUids) throws NoMeetingException, IdentifierNotExistingException;
	
	void addWindowToMeeting(IEventErrorCallback handler, String... windowUids) throws NoMeetingException, IdentifierNotExistingException;
	
	void joinMeeting(String meetingUid) throws WindowInOtherMeetingException, IdentifierNotExistingException;
	
	void kickoutWindowFromMeeting(IEventErrorCallback handler, String windowUid)throws NoMeetingException, IdentifierNotExistingException;
	
	void stopMeeting(IEventErrorCallback handler) throws NoMeetingException;

	void fireMeetingEventToAllOtherWindows(IEvent event, IEventErrorCallback handler)throws NoMeetingException;
	
	void fireMeetingEventToWindows(IEvent event, IEventErrorCallback handler, String...windowUids) throws NoMeetingException, IdentifierNotExistingException;
	
	void closeWindow();
	
	void inviteBuddyToWindow(String buddyUid)throws IdentifierNotExistingException;
	
	void kickoutBuddyFromWindow(String buddyUid)throws IdentifierNotExistingException;
	
	Locale getLocale();
	
	IMessageSender getMessageSender();
		
}
