package com.imseam.chatpage.impl;

import java.util.List;
import java.util.Map;

import com.imseam.chatpage.IActionExecutor;
import com.imseam.chatpage.IParser;

public class PageAction {
	
	private List<IParser> parserList;
	private IActionExecutor actionExecutor;
	private String actionId;
	public String getActionId() {
		return actionId;
	}

	public PageAction(String actionId, List<IParser> parserList, String executorClazz, String methodName, Map<String, String> paramMap) {
		this.actionId = actionId;
		this.parserList = parserList;
		this.actionExecutor = new ActionExecutorWrapper(executorClazz, methodName, paramMap);
	}

	public PageAction(String actionId, List<IParser> parserList, String outcome) {
		this.actionId = actionId;
		this.parserList = parserList;
		this.actionExecutor = new OutcomeActionExecutor(outcome);
	}

	
	public List<IParser> getParserList(){
		return parserList;
	}
	
	public IActionExecutor getActionExcecutor(){
		return actionExecutor;
	}

}
