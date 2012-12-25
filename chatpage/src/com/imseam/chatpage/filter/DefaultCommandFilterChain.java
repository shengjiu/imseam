package com.imseam.chatpage.filter;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;
import com.imseam.common.util.ExceptionUtil;

public class DefaultCommandFilterChain implements CommandFilterChain {
	
	private static Log log = LogFactory.getLog(DefaultCommandFilterChain.class);

	private final ArrayList<CommandFilter> filterList;
	private final CommandFilterChainCallback callback;

	/**
	 * The int which is used to maintain the current position in the filter
	 * chain.
	 */
	private int pos = 0;

	// ----------------------------------------------------------- Constructors
	public DefaultCommandFilterChain(ArrayList<CommandFilter> filterList, CommandFilterChainCallback callback) {
		this.filterList = filterList;
		this.callback = callback;
	}

	// ---------------------------------------------------- FilterChain Methods

	public void doCommandFilter(IChatPage chatpage, IUserRequest request, String input, IMessageSender responseSender) {
		if(pos ==0){
			log.debug("Filter Chain started");
		}

		// Call the next filter if there is one
		if (pos < filterList.size()) {
			CommandFilter filter = filterList.get(pos++);
			try {
				if( filter.parseInput(input, request)){
					filter.doCommandFilter(chatpage, request, input, this, responseSender);
				}
			}
			catch (Throwable e) {
				ExceptionUtil.wrapRuntimeException("Exception happened during filter:" + filter, e);
			}
			return;
		}
		
		log.debug("Filter Chain ended");
		
		if(callback != null){
			callback.filterChainEnded();
		}

	}

	public CommandFilterChainCallback getCallback() {
		return callback;
	}

	public ArrayList<CommandFilter> getFilterList() {
		return filterList;
	}

	public int getPos() {
		return pos;
	}



}
