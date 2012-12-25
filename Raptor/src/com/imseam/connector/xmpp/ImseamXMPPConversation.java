package com.imseam.connector.xmpp;

import java.awt.Color;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;

public class ImseamXMPPConversation extends AbstractMessengerWindow {
	
	private static Log log = LogFactory.getLog(ImseamXMPPConversation.class);
	
	private Chat xmppChat;
	
	public ImseamXMPPConversation(Chat xmppChat, ImseamXMPPConnection connection){
		super(connection, ImseamXMPPConnection.getUserNameWithoutResource(xmppChat.getParticipant()));
		assert(xmppChat != null);
		this.xmppChat = xmppChat;
	}
	
	
	public String getID(){
		return xmppChat.getParticipant();
	}
	
	public void setXMPPChat(Chat chat){
		this.xmppChat = chat;
	}
	
	@Override
	public void sendResponse(IChatletMessage... responses) {
		for(IChatletMessage message : responses){
			assert(message != null);
//			switch(message.getMessageType()){
//			case TextMessage: 
				sendTextMessage(message.getMessageContent().toString());
//				break;
//			default:
//				log.warn(String.format("The message Type(%s) is not supported by the JMSN Connector",message.getMessageType()));
				
//			}
		}
		
	}
	
	private boolean sendTextMessage(String message){
		
		Message xmppMessage = new Message();
		xmppMessage.setBody(message);
		xmppMessage.setProperty("favoriteColor", new Color(34, 43, 255));
		
			
		if(xmppChat != null){
			try{
				xmppChat.sendMessage(xmppMessage);
				log.debug("xmpp send msg ok to :" + xmppChat.getParticipant());
				return true;
			} catch(Exception exp){
				log.warn("xmpp sending Message Error", exp);
				return false;
			}
		}
		log.warn("xmppChat is NULL, so cannot send message!");
		return false;
	}


	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
	}


	public void closeWindow() {
		try{
//			xmppChat.
			super.closeWindow();
		}catch(Exception exp){
			log.warn("The SwitchBoardSession has been null or closed", exp);
		}
		
	}

	public void inviteMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		
	}

	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}


	

}
