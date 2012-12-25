package com.imseam.connector.test.netty;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;
import com.imseam.test.Message;
import com.imseam.test.connector.netty.server.Server;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.TextMessage;

public class NettyMessengerWindow  extends AbstractMessengerWindow  {
	
	private static Log log = LogFactory.getLog(NettyMessengerWindow.class);
	
	private Server server = null;
	
	private String userId;


	protected NettyMessengerWindow(Server server, NettyConnection connection, String userId) {
		super(connection, userId);
		assert(server != null);
		assert(userId != null);
		this.server = server;
		this.userId = userId;
	}

	@Override
	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
	}

	@Override
	public void inviteMessengerUser(String userName) {
		Message message = new InvitationMessage(server.getHostAddress(), userName);
		server.sendMessage(userName, message);
		
	}

	@Override
	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}

	@Override
	public void sendResponse(IChatletMessage... responses) {
		for(IChatletMessage message : responses){
			assert(message != null);
			TextMessage nettyServerTextmessage = new TextMessage(message.getMessageContent().toString(), server.getHostAddress(), this.getWindowContext().getUid());
			server.sendMessage(userId, nettyServerTextmessage);
		}	
	}



}
