package com.imseam.connector.msn.jml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class JMLConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(JMLConnector.class);
	
	public JMLConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new JML connection will be created.");
		return new JMLConnection(application, connectionConfig);
	}

}
