package com.imseam.chatlet;

import java.util.Date;
import java.util.Set;



/// <summary>
/// Interface for bot requests.
/// </summary>
public interface IUserRequest extends IContext {
	
	Date getRequestTimeStamp();

	IChatletMessage getRequestContent();
	
	String getInput();
	
	IChannel getRequestFromChannel();
	
	Set<String> getParameterNames();
	
	String getParameter(String name);
	
	void setParameter(String key, String value);
	
	String removeParameter(String key);


}
