package com.imseam.chatlet;

import java.io.Serializable;

import com.imseam.chatlet.IIdentiable.UidType;

public interface IEventErrorCallback extends Serializable{
	
	String getEventSenderUid();
	
	UidType getSenderIdType();
	
	void handleException(IContext senderContext, String sourceUid, Exception exp);

}
