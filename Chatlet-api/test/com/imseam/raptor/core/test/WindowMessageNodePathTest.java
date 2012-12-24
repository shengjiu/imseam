package com.imseam.raptor.core.test;


import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class WindowMessageNodePathTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testWindowMessageNodePath(){
		
		String windowId = UUID.randomUUID().toString();
		System.out.println("Window id:" + windowId);
		String windowMessageCachePath = "/imseam/windowMessages/" + windowId; 
		int WINDOW_MESSAGE_PATH_CHAR_LENGTH = "/imseam/windowMessages/".length();
		String uuid = UUID.randomUUID().toString();
		char uuidLength = (char)uuid.length();
		
		String messageNodePath = windowMessageCachePath + "/" + uuid + uuidLength ;
		System.out.println("Message node path:" + messageNodePath);
		int indexOfWindowIdEnd = messageNodePath.length() - messageNodePath.charAt(messageNodePath.length() - 1) - 2;
		String parsedWindowId = messageNodePath.substring(WINDOW_MESSAGE_PATH_CHAR_LENGTH, indexOfWindowIdEnd);
		System.out.println("Parsed window id:" + parsedWindowId);
		Assert.assertTrue(windowId.equals(parsedWindowId));
	}

}
