package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;


public abstract class InvocationErrorHandler {
	
	private static Log log = LogFactory.getLog(InvocationErrorHandler.class);
	
	public static void sendExceptionBack(IChatletApplication application, Exception cause, IEventErrorCallback errorCallBack, Date timeStamp, String originalSourceUid, String exceptionSourceUid) {
		if(errorCallBack == null){
			log.trace("Error callback is NULL, sendExceptionBack cancelled, originalSourceUid: "+ originalSourceUid + ", exceptionSourceUid: " + exceptionSourceUid);
			return;
		}
		
		try {
			ErrorHandlerInvocation request = new ErrorHandlerInvocation(errorCallBack, cause, timeStamp, exceptionSourceUid);
			application.getClusterInvocationDistributor().distributeRequest(null, request, errorCallBack.getSenderIdType(), errorCallBack.getEventSenderUid());	
		} catch (IdentifierNotExistingException e) {
			log.warn("Cannot send error back to the original event sender", e);
		}
		
		
	}
}

	
