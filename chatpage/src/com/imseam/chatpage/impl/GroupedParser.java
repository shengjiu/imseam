package com.imseam.chatpage.impl;

import java.util.List;

import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IParser;
import com.imseam.common.util.ExceptionUtil;

public class GroupedParser implements IParser {
	
	//private static Log log = LogFactory.getLog(GroupedParser.class);
	
	private List<IParser> parserList = null;
	private String id;


	
	public GroupedParser(String id, List<IParser> parserList){
		this.id = id;
		this.parserList = parserList;
	}
	
	public ParseResult parseInput(String input, IUserRequest request) {
		for(IParser parser : parserList){
			ParseResult result = parser.parseInput(input, request);
			if(result.isRecognized()){
				return result;
			}
		}
		return null;
	}

	public String getOutcome() {
		ExceptionUtil.createRuntimeException("Function not implemented");
		return null;
	}

	public String getId() {
		return id;
	}

	public String getFireConditionExpression() {
		ExceptionUtil.createRuntimeException("Function not implemented");
		return null;
	}
}
