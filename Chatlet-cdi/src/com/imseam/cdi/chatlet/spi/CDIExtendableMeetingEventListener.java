package com.imseam.cdi.chatlet.spi;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.cdi.chatlet.ext.annotation.meeting.BeforeStartActiveWindow;
import com.imseam.cdi.chatlet.ext.annotation.meeting.JoinedMeeting;
import com.imseam.cdi.chatlet.ext.annotation.meeting.OtherWindowJoinedMeeting;
import com.imseam.cdi.chatlet.ext.annotation.meeting.OtherWindowLeftMeeting;
import com.imseam.cdi.chatlet.ext.annotation.meeting.ReceivedMeetingEvent;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.BuddyNotAvailableForChatException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.chatlet.listener.IMeetingEventListener;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.common.util.ExceptionUtil;


public abstract class CDIExtendableMeetingEventListener implements IMeetingEventListener{
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(CDIExtendableMeetingEventListener.class);
//	public CDIExtendableMeetingEventListener(){
//		
//	}
	
	
	public void onEventReceived(IWindow window, IEvent event){
		ExceptionUtil.createRuntimeException(String.format("Must be extended, the bean can be defined by a chatpage state qualifier annotation and %s annotation", ReceivedMeetingEvent.class));
	}
	
	
	public void onOtherWindowLeftMeeting(IWindow window, String sourceWindowId, String kickoutWindowUid){
		ExceptionUtil.createRuntimeException(String.format("Must be extended, the bean can be defined by a chatpage state qualifier annotation and %s annotation", OtherWindowLeftMeeting.class));
	}
	
	public void onOtherWindowJoinedMeeting(IWindow window, String sourceWindowId, String newWindowUid){
		ExceptionUtil.createRuntimeException(String.format("Must be extended, the bean can be defined by a chatpage state qualifier annotation and %s annotation", OtherWindowJoinedMeeting.class));
	}
	
	public void onJoinedMeeting(IWindow window, String sourceWindowUid){
		ExceptionUtil.createRuntimeException(String.format("Must be extended, the bean can be defined by a chatpage state qualifier annotation and %s annotation", JoinedMeeting.class));
	}
	
	public boolean beforeInviteWindow(IWindow window){
		return window.getMeeting() == null;
	}
	
	public boolean beforeStartActiveWindow(IConnection conneciton, String buddyUid) throws BuddyNotAvailableForChatException, StartActiveWindowException{
		ExceptionUtil.createRuntimeException(String.format("Must be extended, the bean can be defined by a chatpage state qualifier annotation and %s annotation", BeforeStartActiveWindow.class));
		return true;
	}

	@Override
	public void initialize(Object source, Map<String, String> params) {
		ExceptionUtil.createRuntimeException("Should not call or extended");

	}

	@Override
	public void onKickedoutFromMeeting(IWindow window, String sourceWindowId,
			String meetingUid) {
		ExceptionUtil.createRuntimeException("Should not call or extended");
	}

	@Override
	public void onMeetingStopped(IWindow window, String sourceWindowUid) {
		ExceptionUtil.createRuntimeException("Should not call or extended");
	}

}
