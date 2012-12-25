package com.imseam.chatpage.impl;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IParser;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;

public abstract class AbstractPatternBasedParser implements IParser {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AbstractPatternBasedParser.class);

	private String patternString;
	private String id;
	private boolean negative;
	private String fireConditionExpression;
	
	public AbstractPatternBasedParser(String patternString, String id, boolean negative, String fireConditionExpression){
		assert(!StringUtil.isNullOrEmptyAfterTrim(patternString));
		this.patternString = patternString;
		this.id = id;
		this.negative = negative;
//		this.outcome = outcome;
//		if(!StringUtil.isNullOrEmpty(outcome)){
//			this.action = new OutcomeActionExecutor(outcome);
//			if(action != null){
//				log.warn(String.format("The parser(%s:%s) has both Not NULL outcome (%s) and an Action, and the Action will be ignored", id, patternString, outcome));
//			}
//		}
		
		if((fireConditionExpression != null) && 
				(!fireConditionExpression.startsWith("#{") || !fireConditionExpression.endsWith("}") )
			){
			ExceptionUtil.createRuntimeException(String.format("The exception(%) should be formated like #{ab.cd}", fireConditionExpression));
		}

		this.fireConditionExpression = fireConditionExpression;
	}

	
	public String getPatternString(){
		return patternString;
	}

	public String getId() {
		return id;
	}
	
	public boolean isNegative(){
		return negative;
	}

	public String getFireConditionExpression() {
		return fireConditionExpression;
	}

	protected abstract boolean parseInputOnly(String input, IUserRequest request);
	
	public ParseResult parseInput(String input, IUserRequest request) {
		
		boolean found = parseInputOnly(input, request);
        if((isNegative() && !found) || (!isNegative() && found)){
        	return ParseResult.recognized();
        }

		return ParseResult.unRecognized();
	}
	
	
}
