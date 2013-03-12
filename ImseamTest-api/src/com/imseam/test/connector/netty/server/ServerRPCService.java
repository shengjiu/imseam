package com.imseam.test.connector.netty.server;

import java.io.Serializable;


public interface ServerRPCService extends Serializable{

	String startWindow(String... buddies);
	
	void windowStarted(String windowId);
}
