package com.imseam.chatpage.filter;

import com.imseam.common.util.StringUtil;

public class CommandFilterMapping {
	
	private String pathPattern;
	private String filterId;
	
	public CommandFilterMapping(String filterId, String pathPattern){
		assert(!StringUtil.isNullOrEmptyAfterTrim(pathPattern));
		assert(!StringUtil.isNullOrEmptyAfterTrim(filterId));
		this.pathPattern = pathPattern;
		this.filterId = filterId;
	}
	
	public String getFilterId() {
		return filterId;
	}
	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}
	public String getPathPattern() {
		return pathPattern;
	}
	public void setPathPattern(String pathPattern) {
		this.pathPattern = pathPattern;
	}

}
