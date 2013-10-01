package com.imseam.chatlet;

import java.util.Set;



/// <summary>
/// Interface of bot sessions
/// </summary>
public interface ISession extends IContext{
	
	Set<? extends IWindow> getAvailableWindows(); 
	
	void setLiveTime(long liveTime);
	
	long getLiveTime();
	
	IBuddy getBuddy();
	

}
