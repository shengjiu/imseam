package com.imseam.chatpage.impl;

import javax.el.ELContext;
import javax.el.MethodExpression;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatpage.IActionExecutor;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.common.util.StringUtil;

public class OutcomeActionExecutor implements IActionExecutor {

	private String outcome = null;
	public OutcomeActionExecutor(String outcome){
		assert(!StringUtil.isNullOrEmptyAfterTrim(outcome));
		this.outcome = outcome;
	}
	
	@Override
	public String execute(String input, IAttributes request) {
		ChatpageContext context = ChatpageContext.current();
		ELContext elContext = ChatpageContext.current().getELContext();
		
		MethodExpression methodVE = context.getExpressionFactory().createMethodExpression(elContext, outcome, String.class, new Class[]{});

		String outcomeValue = (String)methodVE.invoke(elContext, new Object[]{});
		
		return outcomeValue;
		
	}

}
