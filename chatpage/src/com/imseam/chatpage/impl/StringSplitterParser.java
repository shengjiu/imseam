package com.imseam.chatpage.impl;

import java.util.List;

import com.imseam.chatlet.IUserRequest;

public class StringSplitterParser extends AbstractPatternBasedParser {
	
	private List<String> resultList;
	
	private String paramName;

	public StringSplitterParser(String patternString, String id, boolean negative, String fireConditionExpression, String paramName, List<String> resultList) {
		super(patternString, id, negative, fireConditionExpression);
		assert(resultList != null);
		this.resultList = resultList;
		this.paramName = paramName;
	}

	@Override
	protected boolean parseInputOnly(String input, IUserRequest request) {
		assert(input != null);
		assert(request != null);
		String[] resultStrings = input.split(this.getPatternString());
		if(resultStrings != null && resultStrings.length > 0){
			for(String resultString : resultStrings){
				resultList.add(resultString);
			}
//			request.setParameter(paramName, resultList);
			return true;
		}
		return false;
	}
	
	
	

}
