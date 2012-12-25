package com.imseam.freamworktest.meeting;
/**
 * 1 site
	10 operators
	40 clients
	
	5 operators login online
	5 operators login offline
	clients starts to login (40 clients login )
	1 operator + 2 clients join the meeting
	30 clients waiting
	meeting sent messages + save objects to meeting context
	 each meeting added 2 clients
	10 more clients joined
	meeting sent messages  + save objects to meeting context
	5 offline operators online
	20 more clients added to new meetings
	meeting sent messages  + save objects to meeting context
	first set of meetings stop, clients set to offline and close windows
	clients set to online
	start active window
	meeting sent messages  + save objects to meeting context
	each meeting remove two clients,
	meeting sent messages  + save objects to meeting context
	
	within a site we need checkpoint or waiting point to check test results
 *
 */

public class CommandParser {
	
	
	public void joinMeeting(String operator, String... clients){
		
	}
	
	public void sendMessageToMeeting(String from, String message){
		
	}

	public void saveToMeetingContext(String from, String key, String message){
		
	}
	
	public void removeToMeetingContext(String from, String key){
		
	}

	public void stopMeeting(String from){
		
	}
	
	public void startActiveMeeting(String from, String...clients){
		
	}

	public void addClientToMeeting(String invitor, String... clients){
		
	}

	public void removeClientFromMeeting(String invitor, String... clients){
		
	}

}
