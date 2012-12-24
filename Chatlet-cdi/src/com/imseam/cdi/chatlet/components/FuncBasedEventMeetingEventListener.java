package com.imseam.cdi.chatlet.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.cdi.chatlet.ext.annotation.meeting.BeforeInviteWindow;
import com.imseam.cdi.chatlet.ext.annotation.meeting.ReceivedMeetingEvent;
import com.imseam.cdi.chatlet.spi.CDIExtendableMeetingEventListener;
import com.imseam.cdi.chatlet.spi.FuncBasedEvent;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.event.IEvent;

@ReceivedMeetingEvent
@BeforeInviteWindow
public class FuncBasedEventMeetingEventListener extends CDIExtendableMeetingEventListener {
	private static Log log = LogFactory.getLog(FuncBasedEventMeetingEventListener.class);
	
	@Override
	public void onEventReceived(IWindow window, IEvent event){
		if(event instanceof FuncBasedEvent){
			FuncBasedEvent funcEvent = (FuncBasedEvent)event;
			if(funcEvent.getFunc() == null){
				log.warn("The function based event contains no function");
			}
			funcEvent.getFunc().invoke(window);
			return;
		}
	}
	
	@Override
	public boolean beforeInviteWindow(IWindow window){
		return window.getMeeting() == null;
	}

	
}
