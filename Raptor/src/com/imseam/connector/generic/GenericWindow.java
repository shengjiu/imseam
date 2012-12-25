package com.imseam.connector.generic;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;

public class GenericWindow extends AbstractMessengerWindow {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(GenericWindow.class);

	private IMessengerWindowAdapter windowAdapter;

	public GenericWindow(IMessengerWindowAdapter dialog,
			GenericConnection connection) {
		//the default user need to be considered later
		super(connection, -1, dialog.getMessengerUserUIDs());
		assert (dialog != null);
		this.windowAdapter = dialog;
	}

	@Override
	public void sendResponse(IChatletMessage... responses) {
		
		windowAdapter.sendResponse(responses);
	}
	
	public String getId(){
		return this.windowAdapter.getId();
	}

	@Override
	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
	}

	@Override
	public void inviteMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");

	}

	@Override
	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}

}


