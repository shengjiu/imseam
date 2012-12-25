package com.imseam.raptor;

import java.util.List;

import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IChatlet;
import com.imseam.chatlet.IChatletFilter;
import com.imseam.chatlet.config.ChatletAppConfig;
import com.imseam.chatlet.listener.IMeetingEventListener;
import com.imseam.raptor.cluster.IClusterInvocationDistributor;
import com.imseam.raptor.cluster.IClusterStorage;
import com.imseam.raptor.cluster.IMeetingStorage;

public interface IChatletApplication {

	IApplication getApplicationContext();
	
	void initialize(ChatletAppConfig appConfig);
	
	void startApplication();
	
	void stopApplication();
	
	ISessionManager getSessionManager();
	
//	IMeetingManager getMeetingManager();
	
	IWindowManager getWindowManager();
	
	IRequestProcessor getRequestProcessor();
	
	IEventListenerManager getEventListenerManager();
	
	IClusterInvocationDistributor getClusterInvocationDistributor();
	
	List<IChatletFilter> getFilterList();
	
	IChatlet getChatlet();
	
	IClusterStorage getClusterStorage();
	
	IMeetingStorage getMeetingStorage();
	
//	ILocalObjectFinder getLocalObjectFinder(); 
	
	IMessengerConnection getConnection(String connectionUid);
	
	IMeetingEventListener getMeetingEventListener();
}
