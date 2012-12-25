package com.imseam.chatpage.pageflow;

import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;

import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.common.util.StringUtil;

public class PageflowVariableResolver implements VariableResolver {

	public Object resolveVariable(String name) throws ELException {
//		return ChatPageManager.getInstance().getExpressionSolver().getValue(name);
		assert(!StringUtil.isNullOrEmptyAfterTrim(name));
		if(!name.trim().startsWith("#{")){
			name = "#{" + name +"}";
		}
		return ChatpageContext.current().evaluateExpression(name);
	}

}
