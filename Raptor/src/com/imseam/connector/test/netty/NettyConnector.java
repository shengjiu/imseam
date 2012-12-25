package com.imseam.connector.test.netty;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnector;

public class NettyConnector implements IMessengerConnector {

	@Override
	public NettyConnection createConnection(IChatletApplication application, ConnectionConfig config) {
		
		return new NettyConnection(application, config);
	}

	
}
