package com.imseam.chatpage.pageflow;

import org.jbpm.context.exe.ContextInstance;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;

public abstract class ChatflowVariableUtil {
	final private static String INPUT = "USER_INPUT_VARIABLE_NAME";
	final private static String REQUEST = "USER_REQUEST_VARIABLE_NAME";
	final private static String RESPONSE = "USER_RESPONSE_VARIABLE_NAME";
	
	static public void setInput(ContextInstance processContext, String input){
		processContext.setTransientVariable(INPUT, input);
	}

	static public String getInput(ContextInstance processContext){
		Object inputObject = processContext.getTransientVariable(INPUT);
		if(inputObject != null){
			return (String)inputObject;
		}
		return null;
	}

	static public void setRequest(ContextInstance processContext, IAttributes request){
		processContext.setTransientVariable(REQUEST, request);
	}

	static public IAttributes getRequest(ContextInstance processContext){
		Object requestObject = processContext.getTransientVariable(REQUEST);
		if(requestObject != null){
			return (IAttributes)requestObject;
		}
		return null;
	}
	
	static public void setResponse(ContextInstance processContext, IMessageSender responseSender){
		processContext.setTransientVariable(RESPONSE, responseSender);
	}
	
	static public IMessageSender getResponse(ContextInstance processContext){
		Object responseObject = processContext.getTransientVariable(RESPONSE);
		if(responseObject != null){
			return (IMessageSender)responseObject;
		}
		return null;
	}	
	
}
