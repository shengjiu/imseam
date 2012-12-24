package com.imseam.chatlet;

public interface IIdentiable {
	
	public enum UidType{
		APPICATION,
		WINDOW,
		CHANNEL,
		CONNECTION,
		SESSION,
		BUDDY,
		MEETING,
		USERREQUEST,
		SYSTEMEVENT
	}
	
	String getUid();
	
	UidType getUidType();

}
