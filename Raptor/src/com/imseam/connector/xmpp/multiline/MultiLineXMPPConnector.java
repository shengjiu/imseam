package com.imseam.connector.xmpp.multiline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;

public class MultiLineXMPPConnector implements IMessengerConnector {
	private static Log log = LogFactory.getLog(MultiLineXMPPConnector.class);
	
	public MultiLineXMPPConnector(){
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig connectionConfig){
		log.info("A new Multiline XMPP connection will be created.");
		return new MultiLineXMPPConnection(application, connectionConfig);
	}

}
