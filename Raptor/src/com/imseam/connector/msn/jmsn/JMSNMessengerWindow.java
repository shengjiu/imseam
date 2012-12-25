package com.imseam.connector.msn.jmsn;

import java.awt.Color;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rath.msnm.SwitchboardSession;
import rath.msnm.msg.MimeMessage;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;

public class JMSNMessengerWindow extends AbstractMessengerWindow {
	
	private static Log log = LogFactory.getLog(JMSNMessengerWindow.class);
	
	private SwitchboardSession msnSession;
	
	public JMSNMessengerWindow(SwitchboardSession msnSession, JMSNConnection connection){
		super(connection, msnSession.getLastFriend().getLoginName());
		assert(msnSession != null);
		this.msnSession = msnSession;
	}
	
//	private MSNMessenger getJMSNMessenger(){
//		return ((JMSNConnection)getConnection()).getJMSNMessenger();
//	}
//	
	public String getID(){
		return msnSession.getSessionId();
	}
	
	

	private boolean sendTextMessage(String message){
			MimeMessage mm = new MimeMessage();

			// remember to append the

			// trail to
			// your message
			mm.setMessage(message);
			
			// set the message kind to MESSAGE -_-!!!
			// you have to do this.
			mm.setKind(MimeMessage.KIND_MESSAGE);
			mm.setFontColor(new Color(34, 43, 255));

			if(msnSession != null){
				try{
					msnSession.sendMessage(mm);
					log.debug("send msg ok!");
					return true;
				} catch(Exception e){
					log.warn("Sending Message Error");
					return false;
				}
			}
			log.warn("JMSN msnSession is NULL, so cannot send message!");
//			try{
//				ChannelContext [] channels = this.getOnboardChannels();
//				assert(channels.length > 0);
//				this.getJMSNMessenger().doCall(channels[0].getMessengerUserUID());
//			}catch(Exception e){
//				log.error("Cannot send out message.", e);
//			}
			return false;
	}


	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
	}


	public void inviteMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		
	}

	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}

	@Override
	public void closeWindow() {
		try{
			this.msnSession.close();
			super.closeWindow();
		}catch(Exception exp){
			log.warn("The SwitchBoardSession has been null or closed", exp);
		}
	}

	@Override
	public void sendResponse(IChatletMessage... responses) {
		for(IChatletMessage message : responses){
			assert(message != null);
//				switch(message.getMessageType()){
//				case TextMessage: 
			sendTextMessage(message.getMessageContent().toString());
//					break;
//				default:
//					log.warn(String.format("The message Type(%s) is not supported by the JMSN Connector",message.getMessageType()));
//					
//				}
		}
			
	}	

}


//void setMSNSession(SwitchboardSession newSession){
//	assert(newSession != null);
//	log.info("New MSN session created for the JMSN user" + msnSession.getMsnFriend().getLoginName());
//
//	if((this.msnSession != newSession) || (!msnSession.getSessionId().equalsIgnoreCase(newSession.getSessionId()))){
//		try{
//			msnSession.close();
//		}
//		catch(Exception exp){
//			log.warn("Closing MSN session exception", exp);
//		}
//	}
//	this.msnSession = newSession;
//	
//	
//	boolean waitingFriendsToJoin = false;
//	channelLoop : 
//	for(ChannelContext channel: this.getOnboardChannels()){
//		for(Object friendObj : this.msnSession.getMsnFriends()){
//			MsnFriend friend = (MsnFriend) friendObj;
//			if(friend.getLoginName().equalsIgnoreCase(channel.getMessengerUserName())){
//				continue channelLoop;
//			}
//		}
//		//No friends matched
//		try{
//			msnSession.inviteFriend(channel.getMessengerUserName());
//			waitingFriendsToJoin = true;
//		}catch(Exception exp){
//			log.error("Invite friend join conversation error");
//		}
//		
//	}
//	if(!waitingFriendsToJoin){
//		processSendMessageQueue();
//	}
//}
//
//@Override
//protected void sendFilteredResponse(IChatletMessage... responses) {
//	
//	for(IChatletMessage message : responses){
//		assert(message != null);
//		this.sendMessengeQueue.add(message);
//	}
//	processSendMessageQueue();
//}
//
//public synchronized void processSendMessageQueue(){
//	while(sendMessengeQueue.size() > 0){
//		if(!sendResponseMessage(sendMessengeQueue.peek())){
//			return;
//		}
//		sendMessengeQueue.poll();
//	}
//}
//
//private boolean sendResponseMessage(IChatletMessage message) {
//	switch(message.getMessageType()){
//		case TextMessage: 
//			return sendTextMessage(message.getMessageContent().toString());
//		default:
//			log.warn(String.format("The message Type(%s) is not supported by the Incesoft Connector",message.getMessageType()));
//			
//	}
//	return false;
//}
//
//
//private boolean sendTextMessage(String message){
//		MimeMessage mm = new MimeMessage();
//
//		// remember to append the
//
//		// trail to
//		// your message
//		mm.setMessage(message);
//		
//		// set the message kind to MESSAGE -_-!!!
//		// you have to do this.
//		mm.setKind(mm.KIND_MESSAGE);
//		mm.setFontColor(new Color(234, 243, 255));
//
//		if(msnSession != null){
//			try{
//				msnSession.sendMessage(mm);
//				log.debug("send msg ok!");
//				return true;
//			} catch(Exception e){
//				log.warn("Sending Message Error");
//			}
//		}
//		try{
//			ChannelContext [] channels = this.getOnboardChannels();
//			assert(channels.length > 0);
//			this.getJMSNMessenger().doCall(channels[0].getMessengerUserName());
//		}catch(Exception e){
//			log.error("Cannot send out message.", e);
//		}
//		return false;
//}