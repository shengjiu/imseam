package com.imseam.connector.yahoo.ymsg;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ymsg.network.Session;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.connector.msn.incesoft.IncesoftMSNConversation;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;

public class YMSGConversation extends AbstractMessengerWindow {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(IncesoftMSNConversation.class);
	
	private Session session;
	
	private String userName;
	
	
	public YMSGConversation(Session session,  String userName, IMessengerConnection connection){
		
		super(connection, userName);
		this.session = session;
		this.userName = userName;
	}
	
	
	public String getUserName(){
		return this.userName;
	}

	@Override
	public void sendResponse(IChatletMessage... responses) {
		try{
			for(IChatletMessage message : responses){
				assert(message != null);
//				switch(message.getMessageType()){
//				case TextMessage: 
					this.session.sendMessage(userName, message.getMessageContent().toString());
					break;
//				default:
//					log.warn(String.format("The message Type(%s) is not supported by the Incesoft Connector",message.getMessageType())); 
//				}
			}
		}catch (Exception e) {
            e.printStackTrace();
        }

	}

	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
	}

	public void startConversation() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		
	}

	public void closeWindow() {
		super.closeWindow();
	}

	public void inviteMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		
	}

	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}




}
