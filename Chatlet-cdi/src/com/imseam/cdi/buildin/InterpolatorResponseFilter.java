package com.imseam.cdi.buildin;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletFilter;
import com.imseam.chatlet.IChatletFilterChain;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;

public class InterpolatorResponseFilter implements IChatletFilter{
	
	private static final Log log = LogFactory.getLog(InterpolatorResponseFilter.class);

	public void initialize(Object source, Map<String, String> params) {
		log.info("InterpolatorResponseFilter is initialized");
	}

	@Override
	public void doFilter(IUserRequest request, IMessageSender sender, IChatletFilterChain filterChain) {
		IMessageSender interpolatorSender = new InterpolatorSender(sender);
		filterChain.doFilter(request, interpolatorSender);
	}


}
