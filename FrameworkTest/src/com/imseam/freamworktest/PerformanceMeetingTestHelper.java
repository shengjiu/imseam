package com.imseam.freamworktest;

import java.util.concurrent.LinkedBlockingDeque;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PerformanceMeetingTestHelper {
	
	private LinkedBlockingDeque<String> availableBuddyStack = new LinkedBlockingDeque<String>();
	
	

	public String getAvailableBuddyUid() {
		return availableBuddyStack.poll();
	}

	public void addBuddyUid(String buddyUid) {
		this.availableBuddyStack.addFirst(buddyUid);
	}

}
