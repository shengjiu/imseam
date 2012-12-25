package com.imseam.chatpage.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.IParser.ParseResult;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.common.util.StringUtil;

public class ParserCarrier {
	
	private static Log log = LogFactory.getLog(ParserCarrier.class);
	
	private List<IParser> parserList;
	
	public ParserCarrier(List<IParser> parserList){
		assert(parserList != null);
		this.parserList = parserList;
		
	}
	

	public boolean parseInput(String input, IUserRequest request){
		assert(!StringUtil.isNullOrEmptyAfterTrim(input));
		for(IParser parser : parserList){
			String expression = parser.getFireConditionExpression(); 
			if(( expression != null) &&
					(!evaluateConditionExpression(expression))){
				
				continue;
			}
			
			ParseResult parseResult = parser.parseInput(input, request); 
			if(parseResult.isRecognized()){
				if(parseResult.getParameterMap() != null && parseResult.getParameterMap().size() > 0){
					storeParseResultsToEventContext(parseResult.getParameterMap(), request);
				}
				return true;
			}
		}
		return false;
	}
	
	private void storeParseResultsToEventContext(Map<String, String> resultMap, IAttributes attributes){
		for(String resultKey : resultMap.keySet()){
			attributes.setAttribute(resultKey, resultMap.get(resultKey));
		}
	}
	
	private boolean evaluateConditionExpression(String expression){
		try{
//			Object value = ChatPageManager.getInstance().getExpressionSolver().getValue(expression);
			Object value = ChatpageContext.current().evaluateExpression(expression);
			
			if(value == null){
				log.warn("Expression evaluating result is Null");
				return false;
			}
			if(value instanceof Boolean){
				return (Boolean)value;
			}else if("true".equalsIgnoreCase(value.toString())){
				return true;
			}else{
				return false;
			}
		}catch(Exception exp){
			log.warn("Expression evaluating throws exception", exp);
			return false;
		}
	}

}
