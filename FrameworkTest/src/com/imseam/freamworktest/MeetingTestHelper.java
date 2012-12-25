package com.imseam.freamworktest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MeetingTestHelper {
	
	
	private volatile String firstBuddyUid = null;

	public String getFirstBuddyUid() {
		return firstBuddyUid;
	}

	public void setFirstBuddyUid(String firstBuddyUid) {
		this.firstBuddyUid = firstBuddyUid;
	}

	public void remove(String firstBuddyUid) {
		this.firstBuddyUid = null;
	}

}
