package com.imseam.test;


public interface Connection {
	
	void signOut();
	
//	List<String> getBuddyList();
	
	String startChat(String ... buddies);
	
	void closeWindow(String windowId);
	
	void acceptInvitation(String buddy);
	
	void invite(String buddy);
	
//	String getBuddyStatus(String buddy);
	
	void setSelfStatus(String status);
	
//	String getSelfStatus();
	
//	String getSelfStatusForBuddy(String buddy);
//	
//	void setSelfStatusForBuddy(String buddy, String status);
	
	void addBuddiesToWindow(String windowId, String ... buddies);
	
	void sendMsg(String windowId, String msg);

}
