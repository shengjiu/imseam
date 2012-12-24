package com.imseam.cdi.chatlet.event;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.common.util.StringUtil;

public class FromMeetingRequest  implements IAttributes {
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(FromMeetingRequest.class);
	private IAttributes attributes = new AttributesWrapper();
	private IWindow window;
	private String sourceWindowUid;
	private String meetingUid;
	
	public FromMeetingRequest(){
		
	}
	public FromMeetingRequest(IWindow window){
		this(window, null, null);
	}
	
	public FromMeetingRequest(IWindow window, String sourceWindowUid){
		this(window, sourceWindowUid, null);
	}

	public FromMeetingRequest(IWindow window, String sourceWindowUid, String meetingUid){
		this.window = window;
		this.sourceWindowUid = sourceWindowUid;
		this.meetingUid = meetingUid;
		if(StringUtil.isNullOrEmptyAfterTrim(meetingUid) && window.getMeeting() != null){
			meetingUid = window.getMeeting().getUid();
		}
	}
	
	public FromMeetingRequest(IWindow window,IEvent event){
		this.window = window;
		this.attributes = event;
	}


	public String getSourceWindowId() {
		return sourceWindowUid;
	}

	public void setSourceWindowId(String sourceWindowUid) {
		this.sourceWindowUid = sourceWindowUid;
	}

	public String getMeetingUid() {
		return meetingUid;
	}

	public void setMeetingUid(String meetingUid) {
		this.meetingUid = meetingUid;
	}

	public IWindow getWindow() {
		return window;
	}

	public void setWindow(IWindow window) {
		this.window = window;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.getAttribute(name);
	}

	@Override
	public Set<String> getAttributeNames() {
		return attributes.getAttributeNames();
	}

	@Override
	public Object removeAttribute(String name) {
		return attributes.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.setAttribute(name, o);
	}

	@Override
	public void removeAllAttributes() {
		attributes.removeAllAttributes();
	}
	
	@Override
	public Iterator<String> iterator() {
		return attributes.iterator();
	}
	
}