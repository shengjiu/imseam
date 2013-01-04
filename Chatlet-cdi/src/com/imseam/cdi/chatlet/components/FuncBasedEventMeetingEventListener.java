package com.imseam.cdi.chatlet.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.cdi.chatlet.ext.annotation.meeting.ReceivedMeetingEvent;
import com.imseam.cdi.chatlet.spi.FuncBasedEvent;
import com.imseam.chatlet.IWindow;



public class FuncBasedEventMeetingEventListener{
	private static Log log = LogFactory.getLog(FuncBasedEventMeetingEventListener.class);
	private @Inject Instance<IWindow> window;
	
	
	public void onFunctionBasedEventReceived(@ReceivedMeetingEvent FuncBasedEvent funcEvent){
		if(funcEvent.getFunc() == null){
			log.warn("The function based event contains no function");
		}else{
			funcEvent.getFunc().invoke(window.get());
		}
	}


	
	
}
