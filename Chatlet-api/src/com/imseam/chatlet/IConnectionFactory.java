package com.imseam.chatlet;

import java.util.Collection;

import com.imseam.chatlet.config.ConnectionConfig;


public interface IConnectionFactory {
	
	Collection<ConnectionConfigToConnector> startConnections(IApplication application, Object factoryConfig);
	
	void setConnectionManagementListener(ConnectionManagementListener listener);
	
	interface ConnectionManagementListener{
		void onConnectionConfigCreated(ConnectionConfigToConnector connectionConfigToConnector);
		void onConnectionConfigRemoved(ConnectionConfigToConnector connectionConfigToConnector);
	}
	
	class ConnectionConfigToConnector{
		public String connectorName;
		public ConnectionConfig connectionConfig;
	}
	
	
}
