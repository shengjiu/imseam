package com.imseam.cdi.chatlet.components;

import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.listener.event.ApplicationEvent;


public class CDIRequestObjectInThreadHolder {
	
	private static CDIRequestObjectInThreadHolder instance = new CDIRequestObjectInThreadHolder(); 
	
	private IApplication application;
	
	public static CDIRequestObjectInThreadHolder getInstance(){
		return instance;
	}
	
	private ThreadLocal<Object> requestObjectInThread = new ThreadLocal<Object>(){
        @Override
        protected Object initialValue() {
            return null;
        }
	};
		
	public void setRequestObjectInThread(Object requestObjectInThread){
		this.requestObjectInThread.set(requestObjectInThread);
	}
	
	public Object getRequestObjectInThread(){
		return this.requestObjectInThread.get();
	}	
	
	public void setApplicatonEvent(ApplicationEvent appEvent) {
		application = appEvent.getApplication();
    }
	
	public IApplication getApplication() {
		return application;
	}

//	public void setMessageSender(IMessageSender messageSender){
//		this.messageSender.set(messageSender);
//	}
//	
//	public IMessageSender getMessageSender(){
//		return this.messageSender.get();
//	}

}
