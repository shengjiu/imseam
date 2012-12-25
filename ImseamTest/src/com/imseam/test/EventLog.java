package com.imseam.test;

import java.io.Serializable;
import java.util.Date;

public class EventLog implements Serializable {

	private static final long serialVersionUID = -2302125399847168192L;

	private Date eventTime;
	private String userId;
	private String eventType;
	private String eventContent;

	
	public Date getEventTime() {
		return eventTime;
	}
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventContent() {
		return eventContent;
	}
	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

}
