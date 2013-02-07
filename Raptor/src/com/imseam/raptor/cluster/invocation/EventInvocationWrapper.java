package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.EventTypeEnum;
import com.imseam.raptor.chatlet.ToWindowEventWrapper;
import com.imseam.raptor.cluster.IClusterInvocation;

public class EventInvocationWrapper implements IClusterInvocation<IWindow>{

	private static final long serialVersionUID = 7451637733543329195L;

	private static Log log = LogFactory.getLog(EventInvocationWrapper.class);
	private String sourceUid = null;
	private UidType sourceUidType = null;
	private String targetWindowUid = null;
	private IEvent event = null;
	private Date timeStamp = null;
	
	public EventInvocationWrapper(IEvent event, String targetWindowUid, String sourceUid, UidType sourceUidType, Date timeStamp){
		this.event = event;
		this.sourceUid = sourceUid;
		this.sourceUidType = sourceUidType;
		this.targetWindowUid = targetWindowUid;
		this.timeStamp = timeStamp;
	}

	public String getSourceUid() {
		return sourceUid;
	}

	public UidType getSourceUidType() {
		return sourceUidType;
	}

	@Override
	public void invoke(IChatletApplication application, IWindow window, IEventErrorCallback handler) {
		log.debug(String.format("Start processting %s source(%s:%s), for window(%s)", this.getClass(), sourceUidType, sourceUid, targetWindowUid));
		try{
			if(window == null){
				throw new IdentifierNotExistingException(UidType.WINDOW, targetWindowUid);
			}

			if(application.getEventListenerManager() == null){
				log.warn(String.format("No event listener for window(%s), source(%s:%s)",  targetWindowUid, sourceUidType, sourceUid));
				return;
			}
			
			application.getEventListenerManager().fireEvent(EventTypeEnum.WindowEventRecieved, new ToWindowEventWrapper(window,event));
		}catch(Exception exception){
			try {
				ErrorHandlerInvocation request = new ErrorHandlerInvocation(handler, exception, timeStamp, window.getUid());
				application.getClusterInvocationDistributor().distributeRequest(null, request, handler.getSenderIdType(), handler.getEventSenderUid());
			} catch (IdentifierNotExistingException e) {
				log.warn("Cannot send error back to the original event sender", e);
			}
		}		
		
		
		log.debug(String.format("Done processting %s source(%s:%s), for window(%s)", this.getClass(), sourceUidType, sourceUid, targetWindowUid));
	}
	

	@Override
	public Date getTimestamp() {
		return this.timeStamp;
	}
}
