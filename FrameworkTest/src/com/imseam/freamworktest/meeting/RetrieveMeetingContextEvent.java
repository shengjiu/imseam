package com.imseam.freamworktest.meeting;

import java.util.UUID;

import com.imseam.cdi.chatlet.Id;
import com.imseam.chatlet.listener.event.AbstractEvent;

public class RetrieveMeetingContextEvent extends AbstractEvent {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3688222006850162178L;
	
	private String uid = "RetrieveMeetingContextEvent:::" + UUID.randomUUID().toString();;

	
	private String encodedStr = null;

	public RetrieveMeetingContextEvent(Id sourceId, String encodedStr) {
		super(sourceId);
		this.encodedStr = encodedStr;
	}
	

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public UidType getUidType() {
		return UidType.EVENT;
	}
	
	public String getEncodeStr(){
		return this.encodedStr;
	}

}
