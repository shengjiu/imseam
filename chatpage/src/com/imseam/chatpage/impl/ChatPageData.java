package com.imseam.chatpage.impl;

public class ChatPageData{
	private String fullPathViewId;
	
	private String pageType;
	
	public ChatPageData(String fullPathViewId, String pageType){
		this.fullPathViewId = fullPathViewId;
		this.pageType = pageType;
	}

	public String getPageType() {
		return pageType;
	}

	public String getFullPathViewId() {
		return fullPathViewId;
	}
}