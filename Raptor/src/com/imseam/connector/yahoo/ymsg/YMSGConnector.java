package com.imseam.connector.yahoo.ymsg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class YMSGConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(YMSGConnector.class);
	
	public YMSGConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new YMSG connection will be created.");
		return new YMSGConnection(application, connectionConfig);
	}

}
