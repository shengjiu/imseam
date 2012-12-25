package com.imseam.raptor.chatlet;

import java.util.Locale;

import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IChatletMessage;

public class GenericRequest <T extends IChatletMessage> extends AbstractChatletRequest{


	private T message = null;
	private String uid;
	public GenericRequest(T message, IChannel channelContext,  Locale locale) {
		super(channelContext, locale);
		this.message = message;
		uid = channelContext.getUid() + ":::" + System.currentTimeMillis();
	}


	@Override
	public IChatletMessage getRequestContent() {
		// TODO Auto-generated method stub
		return message;
	}


	@Override
	public String getUid() {
		return uid;
	}


	@Override
	public UidType getUidType() {
		
		return UidType.USERREQUEST;
	}

	



}
