package com.imseam.raptor;

import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.raptor.chatlet.EventTypeEnum;

public interface IEventListenerManager {
	
	void initApplication(IChatletApplication applicaton);
	
	void addListner(ISystemEventListener listener);
	
	void fireEvent(EventTypeEnum eventType, IEvent event);

}
