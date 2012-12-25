package com.imseam.raptor;

import com.imseam.chatlet.config.ConnectionConfig;



/// <summary>
/// Interface for BotConnectors. Currently MSN, Email, Socket, LoadBalance connectors are supported.
/// </summary>
public interface IMessengerConnector {
	
	IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig config);
}
