package com.imseam.chatlet;

import com.imseam.chatlet.config.ConnectionConfig;


public interface IConnector {
	
	IConnection createConnection(IApplication application, ConnectionConfig config);	
}
