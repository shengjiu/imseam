package com.imseam.raptor;

import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IMessageSender;

public interface IChatletExceptionHandler {
	void handleException(IUserRequest chatletRequest, IMessageSender messageSender, Throwable exp);

}
