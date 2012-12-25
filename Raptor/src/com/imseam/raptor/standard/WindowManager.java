package com.imseam.raptor.standard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.IWindowManager;


public class WindowManager implements IWindowManager {
	
	
	private Map<String, IMessengerWindow> windowMap = new ConcurrentHashMap<String, IMessengerWindow>();

	public WindowManager(){
		
	}
	
	@Override
	public void onWindowCreated(IMessengerWindow window) {
		windowMap.put(window.getWindowContext().getUid(), window);
	}
	
	@Override
	public void onWindowStopped(IMessengerWindow window) {
		windowMap.remove(window.getWindowContext().getUid());
	}

	@Override
	public IMessengerWindow getWindowByUid(String windowId) throws IdentifierNotExistingException {
		return this.windowMap.get(windowId);
	}

	@Override
	public void initApplication(IChatletApplication application) {
	}
	
}
