package com.imseam.connector.msn.incesoft;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class IncesoftMSNConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(IncesoftMSNConnector.class);
	
	public IncesoftMSNConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new Incesoft MSN connection will be created.");
		return new IncesoftMSNConnection(application, connectionConfig);
	}

}
