package com.imseam.test;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4581039184210973241L;
	
	private String from = null;
	private Date receivedTime = null;
	private Date sentTime = null;
	private String targetId = null;
	
	public Message(){
		
	}
	
	public Message(String from, String targetId){
		this.from = from;
		this.sentTime = new Date();
		this.targetId = targetId;
	}

	public String getFrom(){
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Date getReceivedTime(){
		return receivedTime;
	}

	public void setReceivedTime(Date date){
		this.receivedTime = date;
	}

	public Date getSentTime(){
		return sentTime;
	}
	
	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}

	public String getTargetId(){
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

}
