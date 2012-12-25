package com.imseam.test;

public interface Connector {
	
	Connection login(String username, String password, String status, IEventListener listener);

}
