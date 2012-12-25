package com.imseam.connector.msn.jmsn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class JMSNConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(JMSNConnector.class);
	
	public JMSNConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new JMSN connection will be created.");
		return new JMSNConnection(application, connectionConfig);
	}

}
