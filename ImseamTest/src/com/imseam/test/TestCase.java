package com.imseam.test;

import java.util.Date;

public abstract class TestCase {
	
	private Date startTime;
	
	private User user;
	
	protected TestCase(User user, Date startTime){
		this.user = user;
		this.startTime = startTime;
	}
	
	public User getUser() {
		return user;
	}

	public Date getStartTime() {
		return startTime;
	}
	
	abstract void run();

	/**
	 * Goals:
	 * 1. the performance chart of single server for echo
	 * 2. the performance chart of single server for meeting
	 * 3. the performance chart of clustered server for meeting
	 * 4. the chart of connection numbers to response speed
	 * 5. the average messages throughput to response time, find out when response time starts to increase sharply
	 * 
	 */
	
	/**
	 * Test utility functions
	 * start a window
	 * number of windows
	 * send message
	 * receive message
	 * record time
	 * sleep
	 */
	
	/**
	 * Project
	 * Test utility
	 * Test connector
	 * Echo chat APP/Test
	 * Meeting chat APP/Test
	 * 
	 */
	
	/**
	 * Each test case starts number of connections to the server
	 * is it possible to use pseudo connections? no, since the connections will consume performance
	 * rmi/rpc connections?
	 */
	
	/** A. echo
	 * 1. send a message
	 * 2. echo back the message
	 * 3. record the time cost
	 * 4. sleep 
	 * 5. repeat
	 */
	
	/** B. meeting
	 * 1. send a message
	 * 2. start a meeting 
	 * 2. echo back the message
	 * 3. record the time cost
	 * 4. sleep 
	 * 5. repeat
	 */	

}
