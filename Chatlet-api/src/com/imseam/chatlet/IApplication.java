package com.imseam.chatlet;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.cluster.IClusterCache;



/// <summary>
/// Interface of BotApplications. A bot server can host multiple BotApplications
/// </summary>
public interface IApplication extends IContext {
	
	String getApplicationName();
	
	String getApplicationRootPath();
	
	URL getResourceURL(String resource);
	
	String getInitParam(String paramName);
	
	ResourceBundle getResourceBundle(Locale locale);
	
	void fireSystemEventToWinodw(String windowUid, IEvent event, IEventErrorCallback handler, String sourceUid, UidType sourceUidType) throws IdentifierNotExistingException;
	
	IClusterCache getClusterCache();
	
}
