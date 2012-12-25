package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IClusterInvocation;

public class ErrorHandlerInvocation implements IClusterInvocation<IContext>{


	private static final long serialVersionUID = 5323666829097863608L;
	private static Log log = LogFactory.getLog(ErrorHandlerInvocation.class);
	private Exception exception = null;
	private Date timeStamp;
	private String orginalTargetUid = null;
	private IEventErrorCallback handler = null;
	
	public ErrorHandlerInvocation(IEventErrorCallback handler, Exception exception, Date timeStamp, String orginalTargetUid){
		this.exception = exception;
		this.timeStamp = timeStamp;
		this.orginalTargetUid = orginalTargetUid;
		this.handler = handler;
	}



	@Override
	public void invoke(IChatletApplication application, IContext context, IEventErrorCallback handler){
		if(context == null){
			log.warn("The context is null");
			return;
		}
		
		log.debug(String.format("Start processting %s error for context(%s)", this.getClass(), context.getUid()));
		
		this.handler.handleException(context, orginalTargetUid, exception);
		
		log.debug(String.format("Finished processting %s error for window(%s)", this.getClass(), context.getUid()));
	}



	@Override
	public Date getTimestamp() {
		return timeStamp;
	}
	
}
