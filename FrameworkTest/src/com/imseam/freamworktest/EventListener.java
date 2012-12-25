package com.imseam.freamworktest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.imseam.cdi.chatlet.ext.annotation.ApplicationInitialized;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatpage.pageflow.JbpmManager;

@ApplicationScoped
public class EventListener {
	
	
	public void onApplicationInitialized(@Observes @ApplicationInitialized ApplicationEvent event) throws Exception{
		//JbpmFlowDefinitionManager.getInstance().startup("echo-foreach-menu-chatflow.xml");
		JbpmManager.getInstance().startup("performancetest-chatflow.xml");
	}
	

}
