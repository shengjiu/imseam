package com.imseam.chatpage.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageRenderException;
import com.imseam.chatpage.IResponseRender;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;

//only page body can be included

public class ExpressionRender implements IResponseRender{
	
	
	private static Log log = LogFactory.getLog(ExpressionRender.class);
	
	private final String expression;
	
	public ExpressionRender(String expression){
		assert(!StringUtil.isNullOrEmptyAfterTrim(expression));
		if((!expression.startsWith("#{")) || (!expression.endsWith("}"))){
			ExceptionUtil.createRuntimeException(String.format("The exception(%) should be formated like #{ab.cd}", expression));
		}
		this.expression = expression; 
		
	}

	public void render(String input, IAttributes request, IMessageSender responseSender) throws ChatPageRenderException {
		try{
//			Object value = ChatPageManager.getInstance().getExpressionSolver().getValue(expression);
			Object value = ChatpageContext.current().evaluateExpression(expression);
			if(value == null){
				log.warn("Expression evaluating result is Null");
				return;
			}
			if(value instanceof IResponseRender){
				((IResponseRender)value).render(input, request, responseSender);
			}else{
				log.debug("Expression evaluating result is not ResponseRender, toString will be used");
				responseSender.send(value.toString());
			}
		}catch(Exception exp){
			throw new ChatPageRenderException("Expression evaluating throws exception", exp);
		}
		
	}

}
