package com.imseam.cdi.chatlet.annotations;

import com.imseam.cdi.chatlet.ext.annotation.BuddySignOff;


/*
 * For a buddy with multiple active windows open the buddy status change may be invoked multiple times
 * but each invocation will have different window in context
 * for the event only have connection context, the annotation need to be as BuddyStatusChange(chatflow=null, state=null) 
 * 
 * fireevent to connection
 * loop to fireevent to each active window
 * 
 */
public class BuddySignOffAnnotation extends AbstractChatletEventAnnotation<BuddySignOff> implements BuddySignOff{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3719875906454171359L;

	public BuddySignOffAnnotation() {
	}

}
