package com.imseam.connector.msn.jml;

import java.util.Locale;

import net.sf.jml.Email;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.message.MsnInstantMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;

public class JMLMessengerWindow extends AbstractMessengerWindow {
	
	private static Log log = LogFactory.getLog(JMLMessengerWindow.class);
	
	private MsnSwitchboard msnSession;
	

	
	private static int MESSAGE_COLOR = 1 * 128 * 255;
	
	public JMLMessengerWindow(MsnSwitchboard msnSession, JMLConnection connection){
		super(connection, msnSession.getAllContacts()[0].getEmail().getEmailAddress());
		assert(msnSession != null);
		this.msnSession = msnSession;
	}
	

	
	MsnSwitchboard getMsnSwitchboard(){
		return this.msnSession;
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
//				
//			}
		}
		
	}
	
	
	
	private boolean sendTextMessage(String message){
		MsnInstantMessage  mm = new MsnInstantMessage();

		mm.setContent(message);

		// set the message kind to MESSAGE -_-!!!
		// you have to do this.

		mm.setFontRGBColor(MESSAGE_COLOR);

		if (msnSession != null) {
			try {
				msnSession.sendMessage(mm);
				log.debug("send msg ok!");
				return true;
			} catch (Exception e) {
				log.warn("Sending Message Error");
				return false;
			}
		}
		log.warn("JML msnSession is NULL, so cannot send message!");

		return false;
	}


	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
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
	public void inviteMessengerUser(String userName) {
		try{
			this.msnSession.inviteContact(Email.parseStr(userName));
			super.closeWindow();
		}catch(Exception exp){
			log.warn("invite messenger user exception", exp);
		}	
	}

	@Override
	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}

}

