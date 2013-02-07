package com.imseam.chatlet.listener;

import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IInitable;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.BuddyNotAvailableForChatException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.chatlet.listener.event.IEvent;


public interface IMeetingEventListener extends IInitable{
	
	
	void onKickedoutFromMeeting(IWindow window, String sourceWindowId, String meetingUid);
	
	void onOtherWindowLeftMeeting(IWindow window, String sourceWindowId, String kickoutWindowUid);
	
	void onOtherWindowJoinedMeeting(IWindow window, String sourceWindowId, String newWindowUid);
	
	void onJoinedMeeting(IWindow window, String sourceWindowUid);
	
	void onMeetingStopped(IWindow window, String sourceWindowUid);
	
	void onEventReceivedInMeeting(IWindow window, IEvent event);
	
	//not synchrozied
	boolean beforeInviteWindow(IWindow window);
	
	//not synchrozied
	boolean beforeStartActiveWindow(IConnection conneciton, String buddyUid)throws BuddyNotAvailableForChatException, StartActiveWindowException;
	
}
