package com.imseam.test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.imseam.test.message.TextMessage;

public class Window {
	
	private User user;
	
	private Connection connection = null;
	
	private List<String> buddyList = null;
	
	private String windowId = null;
	
	public Window(String windowId, User user, String ...buddies){
		assert(user != null);
		this.user = user;
		this.connection = user.getConnection();
		this.buddyList = Arrays.asList(buddies);
		this.windowId = windowId;
	}

	public List<String> getBuddyListInWindow(){
		return buddyList;
	}
	
	public void inviteBuddy(String ... buddies){
		connection.addBuddiesToWindow(windowId, buddies);
	}
	
	public void sendMsg(String msg){
		connection.sendMsg(windowId, msg);
	}
	
	public void idle(int min, int max){
		assert(max >= min);
		try {
			Thread.sleep((min + (new Random()).nextInt(max - min)) * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public TextMessage waitForTextMessage(int milliseconds) throws WaitException{
		return user.waitForTextMessage(windowId, milliseconds);
	}
	
	public void buddyAddedToWindow(String ...buddyIds){
		buddyList.addAll(Arrays.asList(buddyIds));
	}
	
	public String windowId(){
		return windowId;
	}
	
	public void closeWindow(){
		user.closeWindow(windowId);
	}
}
