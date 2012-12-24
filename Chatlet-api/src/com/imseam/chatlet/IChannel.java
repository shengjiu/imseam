package com.imseam.chatlet;

import java.util.Locale;



public interface IChannel extends IContext{
	
	ISession getUserSession();
	
	IWindow getWindow();
	
	IBuddy getBuddy();
	
	Locale getLocale();
	
}
