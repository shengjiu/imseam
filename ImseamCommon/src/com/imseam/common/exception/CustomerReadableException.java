package com.imseam.common.exception;

import com.imseam.common.util.StringUtil;

public class CustomerReadableException extends TracableRuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2526278328501121269L;
	
	private String customerMsgKey;
	
	public CustomerReadableException(String loggingMsg, String customerMsgKey) {
		super(loggingMsg);
		this.customerMsgKey = customerMsgKey;
		assert(!StringUtil.isNullOrEmptyAfterTrim(loggingMsg));
	}
	
	public CustomerReadableException(String loggingMsg, String customerMsgKey, Throwable cause) {
		super(loggingMsg, cause);
		this.customerMsgKey = customerMsgKey;
		assert(!StringUtil.isNullOrEmptyAfterTrim(loggingMsg));
		assert(!StringUtil.isNullOrEmptyAfterTrim(loggingMsg));

	}
	
	public String getCustomerMsgKey(){
		return customerMsgKey;
	}
	
}
