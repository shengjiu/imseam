package com.imseam.connector.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class GenericConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(GenericConnector.class);
	
	public GenericConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new Java Messenger connection will be created.");
		return new GenericConnection(application, connectionConfig);
	}

}
