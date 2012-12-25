package com.imseam.raptor.standard;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IWindow;
import com.imseam.common.exception.CustomerReadableException;
import com.imseam.raptor.IChatletExceptionHandler;
import com.imseam.raptor.chatlet.MessengerTextMessage;
import com.imseam.raptor.internalization.ResourceBundleHelper;

public class ChatletExceptionHandler implements
		IChatletExceptionHandler {
	private static Log log = LogFactory
			.getLog(ChatletExceptionHandler.class);

	public void handleException(IUserRequest chatletRequest, IMessageSender messageSender, Throwable exp) {
		assert (exp != null);
		assert (chatletRequest != null);

		IWindow window = chatletRequest.getRequestFromChannel().getWindow();
		assert (window != null);

		if (exp instanceof CustomerReadableException) {
			
			CustomerReadableException crExp = (CustomerReadableException) exp;
			String uniqueID = crExp.getUniqueID();
			String customerMessageKey = crExp.getCustomerMsgKey();
			Locale locale = window.getLocale();

			String customerMsg = ResourceBundleHelper.getInstance().
				getExceptionMessageWithUniqueID(locale, uniqueID, customerMessageKey, chatletRequest);

			MessengerTextMessage response = new MessengerTextMessage(customerMsg);
			log.error(String.format("An exception happened: %s", exp.getMessage()), exp);

			try {
				messageSender.send(response);
			} catch (Exception sendMsgExp) {
				log.error(String.format("An exception happened when sending a response message(%s)", response), sendMsgExp);
			}

			return;
		}
		
		log.error(String.format("An exception happened when processing request with ID (%s)", 
				chatletRequest.getUid()), exp);
	}

}
