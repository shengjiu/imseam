package com.imseam.test.connector.netty;

import java.util.logging.Logger;

import com.imseam.test.Connection;
import com.imseam.test.Connector;
import com.imseam.test.IEventListener;

public class RPCConnector implements Connector {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RPCConnector.class.getName());

	private String host;
	private int port;
	
	public RPCConnector(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	@Override
	public Connection login(String username, String password, String status, IEventListener listener) {
		NettyClientManager.instance().connect(host, port, username, status, listener);
		return new RPCConnection(username);
	}
	
	

}
