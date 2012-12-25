package com.imseam.raptor.chatlet;

import java.util.Locale;

import com.imseam.chatlet.IChannel;

public class MessengerTextRequest extends AbstractChatletRequest{


	private MessengerTextMessage message = null;
	public MessengerTextRequest(MessengerTextMessage message, IChannel channelContext, Locale locale) {
		super(channelContext, locale);
		this.message = message;
	}

	@Override
	public MessengerTextMessage getRequestContent() {
		return message;
	}

	@Override
	public String getUid() {
		return super.getRequestUid();
	}

	@Override
	public UidType getUidType() {
		return UidType.USERREQUEST;
	}

	@Override
	public String toString() {
		return "MessengerTextRequest [message=" + message + ", toString()=" + super.toString() + "]";
	}
	
	


}
