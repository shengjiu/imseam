package com.imseam.chatlet;

import java.util.Set;



/// <summary>
/// Interface of bot sessions
/// </summary>
public interface ISession extends IContext{
	
	Set<? extends IWindow> getAvailableWindow(); 
	
	void setLiveTime(long liveTime);
	
	long getLiveTime();
	
	IBuddy getBuddy();
	

}
