package com.imseam.raptor.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IEventListenerManager;
import com.imseam.raptor.chatlet.EventTypeEnum;

public class EventListenerManager implements IEventListenerManager{

	private static Log log = LogFactory.getLog(EventListenerManager.class);
	
	private List<ISystemEventListener> eventListenerList = new ArrayList<ISystemEventListener>();

	public EventListenerManager(){
		
	}
	
	
	
	public void addListner(ISystemEventListener listener){
		if(listener == null){
			ExceptionUtil.createRuntimeException("The listener Object is null.");
		}
		log.info(String.format("Add a event listener (%s)", listener.getClass()));

		eventListenerList.add(listener);
	}
	
	public void fireEvent(EventTypeEnum eventType, IEvent event){
		assert(eventType != null);
		assert(event != null);
		if(!eventType.getEventObjectClass().isAssignableFrom(event.getClass())){
			log.warn(String.format("The event class (%s) is not match type defined by %s", event.getClass(), eventType));
			return;
		}
		
		for(ISystemEventListener listener : this.eventListenerList){
			try{
				eventType.fireEvent(listener, event);
			}catch(Exception exp){
				log.warn(String.format("eventtype: %s, event: %s, listener: %s", eventType, event, listener), exp);
			}
		}
	}



	@Override
	public void initApplication(IChatletApplication applicaton) {
		// TODO Auto-generated method stub
		
	}
	

}

