package com.imseam.connector.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class ImseamXMPPConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(ImseamXMPPConnector.class);
	
	public ImseamXMPPConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new XMPP connection will be created.");
		return new ImseamXMPPConnection(application, connectionConfig);
	}

}
