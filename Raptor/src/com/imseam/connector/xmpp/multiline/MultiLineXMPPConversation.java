package com.imseam.connector.xmpp.multiline;

import java.awt.Color;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;

public class MultiLineXMPPConversation extends AbstractMessengerWindow {
	
	private static Log log = LogFactory.getLog(MultiLineXMPPConversation.class);
	
	private Chat xmppChat;
	
	private XMPPConnection xmppConnection;
	
	final private String participantID;
	
	public MultiLineXMPPConversation(String participantID, Chat xmppChat, MultiLineXMPPConnection connection, XMPPConnection xmppConnection){
		super(connection, participantID);
		assert(xmppChat != null);
		assert(xmppConnection != null);
		this.xmppChat = xmppChat;
		this.xmppConnection = xmppConnection; 
		this.participantID = participantID;
	}
	
	
	public String getParticipantID(){
		return this.participantID;
	}
	
	public void setXMPPChat(Chat chat){
		this.xmppChat = chat;
	}
	
	XMPPConnection getXMPPConnection(){
		return xmppConnection; 
	}
	

	@Override
	public void sendResponse(IChatletMessage... responses) {
		
		String textResponse = "";
		
		for(IChatletMessage message : responses){
			assert(message != null);
//			switch(message.getMessageType()){
//			case TextMessage: 
				textResponse += message.getMessageContent().toString() + "\n";
//				break;
//			default:
//				log.warn(String.format("The message Type(%s) is not supported by the XMPP Connector",message.getMessageType()));
//				
//			}
		}
		sendTextMessage(textResponse);
		
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
			super.closeWindow();
		}catch(Exception exp){
			log.warn("Cannot close the multilineXMPPConversation", exp);
		}
		
	}

	public void inviteMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		
	}

	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}

}
