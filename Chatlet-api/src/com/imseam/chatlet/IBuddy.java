package com.imseam.chatlet;



public interface IBuddy extends IIdentiable{
	
	String getConnectionUid();
	
	String getUserId();
	
	BuddyStatusEnum getBuddyStatus();
	
}
