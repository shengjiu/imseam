package com.imseam.chatpage.impl;

import java.lang.reflect.Method;
import java.util.Map;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatpage.IActionExecutor;
import com.imseam.common.util.ClassUtil;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;

public class ActionExecutorWrapper implements IActionExecutor {

//	private String clazz = null;
//	private String methodName = null;
	private IActionExecutor action = null;
	private Method method = null;
//	private Map<String, String> paramMap = null;
	
	public ActionExecutorWrapper(String clazz, String methodName, Map<String, String> paramMap){
		assert(!StringUtil.isNullOrEmptyAfterTrim(clazz));
		assert(!StringUtil.isNullOrEmptyAfterTrim(methodName));
//		this.clazz = clazz;
//		this.methodName = methodName;
//		this.paramMap = paramMap;
		if((paramMap != null) && (paramMap.size() > 0)){
			action = (IActionExecutor)ClassUtil.createInstance(clazz, new Class[]{Map.class}, paramMap);
		}else{
			action = (IActionExecutor)ClassUtil.createInstance(clazz);
		}
		try {
			method = action.getClass().getMethod(methodName, IAttributes.class);
			if(!method.getReturnType().equals(String.class)){
				ExceptionUtil.createRuntimeException(String.format("Expecting method(%s) return String, but actually return %s", methodName, method.getReturnType()));
			}
		} catch (Exception e) {
			ExceptionUtil.wrapRuntimeException(e);
		}
	}
	
	@Override
	public String execute(String input, IAttributes request) {
		String outcome = null;
		try {
			outcome = (String) method.invoke(action, input, request);
		} catch (Exception e) {
			ExceptionUtil.wrapRuntimeException(e);
		}
		return outcome;
	}

}
