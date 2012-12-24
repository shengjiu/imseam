package com.imseam.cdi.chatlet.spi;

import java.util.HashMap;
import java.util.Map;

import com.imseam.cdi.chatlet.Id;
import com.imseam.chatlet.IContext;
import com.imseam.common.util.StringUtil;

public class ChatflowStateBasedFunc extends Func{

	private static final long serialVersionUID = 284113804688117693L;

	
	
	private Map<String, Map<String, Func>> chatflowFunctionMaps = new HashMap<String, Map<String, Func>>();
	
	static final String NullKey = ChatflowStateBasedFunc.class + ":null";

	public ChatflowStateBasedFunc(Id sourceId){
		super(sourceId);
	}
	
	public void invoke(IContext context){
		String currentFlowName = null;
		
		Map<String, Func> stateFunctionMap = chatflowFunctionMaps.get(currentFlowName);
		
		if(stateFunctionMap == null){
			stateFunctionMap = chatflowFunctionMaps.get(NullKey);
		}
		
		if(stateFunctionMap != null){
			String currentState = null;
			Func function = stateFunctionMap.get(currentState);
			if(function == null){
				function = stateFunctionMap.get(NullKey);
				if(function != null){
					function.invoke(context);
				}
			}
		}
	}

	
	
	private static String getKey(String key){
		return StringUtil.isNullOrEmptyAfterTrim(key)? NullKey : key.trim();
		
	}
	public ChatflowStateBasedFunc addFunction(Id sourceId, Func function){
		return addFunction(null, null, function);
	}
	
	public ChatflowStateBasedFunc addFunction(Id sourceId, String state, Func function){
		return addFunction(null, state, function);
	}
	
	public ChatflowStateBasedFunc addFunction(Id sourceId, String flowName, String state, Func function){
		
		flowName = getKey(flowName);
		state = getKey(flowName);
		
		
		Map<String, Func> stateFunctionMap = chatflowFunctionMaps.get(flowName);
		if(stateFunctionMap == null){
			stateFunctionMap = new HashMap<String, Func>();
			chatflowFunctionMaps.put(flowName, stateFunctionMap);
		}
		stateFunctionMap.put(state, function);
		return this;
	}
	
}
