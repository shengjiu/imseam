package com.imseam.chatlet;


public interface IStartActiveWindowCallback {
	
	void windowStarted(IChannel channel);
	void startWindowFailed(Exception cause);
}
