package com.imseam.test.connector.netty.server;

public abstract class ServerRPCServiceFactory{
	
	private static ServerRPCServiceFactory factory = null;
	
	public static ServerRPCServiceFactory get(){
		return factory;
	}
	
	public static void setFactory(ServerRPCServiceFactory theFactory){
		factory = theFactory;
	}
	
	public abstract ServerRPCService getService(String username);

}
