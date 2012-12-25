package com.imseam.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;

import com.imseam.common.exception.CustomerReadableException;

public class ExceptionUtil {

	public static void handleRemoteException(RemoteException re) throws RuntimeException{
		if(re.detail!= null) {
			throw new RuntimeException(re.detail);
		}
	}

	/**
	 * This method takes a exception as an input argument and returns the
	 * stacktrace as a string.
	 */
	public static String getStackTrace(Throwable exception) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		return sw.toString();
	}

	
	public static void createRuntimeException(String str) {
		throw new RuntimeException(str);
	}
	
	public static void createRuntimeException(String format, Object ... args) {
		throw new RuntimeException(String.format(format, args));
	}
	
	public static void assertStringNotNullOrEmptyAfterTrim(String str, String msgFormat, Object ... args){
		if(StringUtil.isNullOrEmptyAfterTrim(str)){
			ExceptionUtil.createRuntimeException(msgFormat, args);
		}
	}

	public static void assertNotNull(Object value, String msgFormat, Object ... args){
		if(value == null){
			ExceptionUtil.createRuntimeException(msgFormat, args);
		}
	}

	
	public static void wrapRuntimeException(Throwable e) {
		throw new RuntimeException(e);
	}
	
	public static void wrapRuntimeException(String msg, Throwable e) {
		throw new RuntimeException(msg, e);
	}
	
	public static void createCustomerReadableException(String loggingMsg, String customerMsgKey) {
		throw new CustomerReadableException(loggingMsg, customerMsgKey);
	}
	
	
	public static void wrapCustomerReadableException(String loggingMsg, String customerMsgKey, Throwable e) {
		throw new CustomerReadableException(loggingMsg, customerMsgKey, e);
	}
	
}
