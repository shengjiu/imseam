package com.imseam.raptor.chatlet;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatlet;
import com.imseam.chatlet.IChatletFilter;
import com.imseam.chatlet.IChatletFilterChain;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.common.util.ExceptionUtil;

public class FilterChain implements IChatletFilterChain {

	private static Log log = LogFactory.getLog(FilterChain.class);

	private final List<IChatletFilter> filterList;
	private final IChatlet chatlet;
	private int pos = 0;

	public FilterChain(List<IChatletFilter> filterList, IChatlet chatlet) {
		this.filterList = filterList;
		this.chatlet = chatlet;
	}

	@Override
	public void doFilter(IUserRequest request, IMessageSender sender) {
		if (pos == 0) {
			log.debug("Filter Chain started");
		}

		// Call the next filter if there is one
		if (pos < filterList.size()) {
			IChatletFilter filter = filterList.get(pos++);
			try {
				filter.doFilter(request, sender, this);
			} catch (Throwable e) {
				ExceptionUtil.wrapRuntimeException("Exception happened during filter:" + filter, e);
			}
			return;
		}

		log.debug("Filter Chain ended");

		chatlet.serviceUserRequest(request, sender);
	}
}
