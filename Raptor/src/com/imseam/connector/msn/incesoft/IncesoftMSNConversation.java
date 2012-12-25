package com.imseam.connector.msn.incesoft;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletMessage;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.chatlet.AbstractMessengerWindow;
import com.incesoft.botplatform.sdk.RobotException;
import com.incesoft.botplatform.sdk.RobotSession;

public class IncesoftMSNConversation extends AbstractMessengerWindow {
	
	private static Log log = LogFactory.getLog(IncesoftMSNConversation.class);
	
	private RobotSession robotSession;
	
	public IncesoftMSNConversation(RobotSession robotSession, IMessengerConnection connection){
		super(connection, robotSession.getUser().getID());
		this.robotSession = robotSession;
		
	}
	

	@Override
	public void sendResponse(IChatletMessage... responses) {
		try{
			for(IChatletMessage message : responses){
				assert(message != null);
//				switch(message.getMessageType()){
//				case TextMessage: 
					this.robotSession.send(message.getMessageContent().toString());
//					break;
//				default:
//					log.warn(String.format("The message Type(%s) is not supported by the Incesoft Connector",message.getMessageType())); 
//				}
			}
		}catch (RobotException e) {
            e.printStackTrace();
        }

	}
	
	public void closeWindow() {
		try{
			this.robotSession.close();
			super.closeWindow();
		}catch(Exception exp){
			log.warn("The SwitchBoardSession has been null or closed", exp);
		}
		
	}

	public Locale getLocale() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return null;
	}

	public RobotSession getRobotSession(){
		return this.robotSession;
	}

	public void inviteMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		
	}

	public void kickoutMessengerUser(String userName) {
		ExceptionUtil.createRuntimeException("Method is not implemented");
	}



}
