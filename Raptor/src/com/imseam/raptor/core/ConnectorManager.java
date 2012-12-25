package com.imseam.raptor.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerConnector;


public class ConnectorManager {

	private static Log log = LogFactory.getLog(ConnectorManager.class);

	private static ConnectorManager instance = new ConnectorManager();
	
	private Map<String, IMessengerConnector> connectorMap = new HashMap<String, IMessengerConnector>();
	
	
	
	private ConnectorManager()
	{
		log.debug("A ConnectorManager Object is created.");
	}
	
	public static ConnectorManager instance()
	{
		return instance;
	}
	
	public void addConnector(String name, IMessengerConnector connector){
		if(connectorMap.get(name) != null){
			log.warn("The connector " + name +" is already existing!");;
		}
		connectorMap.put(name, connector);
	}
	
	public Collection<IMessengerConnector> getAllConnectors(){
		return connectorMap.values();
	}
	
	public IMessengerConnection createConnection(IChatletApplication application, ConnectionConfig config)
	{
		String connectorName = config.getConnectorRef();
		assert(!StringUtil.isNullOrEmptyAfterTrim(connectorName));
		IMessengerConnector connector = connectorMap.get(connectorName);
		if(connector == null){
			ExceptionUtil.createRuntimeException("The connector " + connectorName + " is NOT existing!");
		}
		IMessengerConnection connection = connector.createConnection(application, config);
		connection.initialize();
		log.info(String.format("The ConnectorManager is creating a new connection (connector name: %s)", connectorName));
		assert(connection != null);
		return connection;
	}

}
