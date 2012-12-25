package com.imseam.chatpage.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IUserRequest;

public class StringParser extends AbstractPatternBasedParser {
	private static Log log = LogFactory.getLog(StringParser.class);
	private static final String SEPERATOR = "|";
	private static final String ANYTHING = "*";
	private String[] tokens = null; 
	private boolean anyInput = false;

	public StringParser(String patternString,String id, boolean negative, String fireConditionExpression){
		super(patternString, id, negative, fireConditionExpression);
		
		if(ANYTHING.equals(patternString)){
			anyInput = true;
			return;
		}
		
		try{
			tokens = patternString.toLowerCase().split("\\" + SEPERATOR);
		}catch(Exception exp){
			log.error(String.format("The String pattern (%s) cannot be splited by %s.", patternString, SEPERATOR), exp);
		}
	}
	
	
	public StringParser(String patternString){
		this(patternString, null, false, null);
	}
	
	public boolean parseInputOnly(String input, IUserRequest request) {
		assert(input != null);
		assert(request != null);
		
		if(anyInput) return true;
		
		for(String token: tokens){
			if(input.equalsIgnoreCase(token)){
				return true;
			}
		}
        
        return false; 
	}
	
	public String [] getTokens(){
		return this.tokens;
	}
	
	public String getSeperator(){
		return SEPERATOR;
	}


	

}
