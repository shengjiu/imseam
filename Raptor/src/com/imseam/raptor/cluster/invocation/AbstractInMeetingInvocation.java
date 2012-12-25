package com.imseam.raptor.cluster.invocation;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IIdentiable.UidType;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.exception.NoMeetingException;
import com.imseam.chatlet.exception.WindowInOtherMeetingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IClusterInvocation;
import com.imseam.raptor.cluster.NoMeetingListenerException;

public abstract class AbstractInMeetingInvocation implements IClusterInvocation<IWindow>{

	private static final long serialVersionUID = 7451637733543329195L;

	private static Log log = LogFactory.getLog(AbstractInMeetingInvocation.class);
	private String meetingUid = null;
	private String sourceWindowUid = null;
	private String targetWindowUid = null;
	private Date timeStamp = null;
	
	protected AbstractInMeetingInvocation(String meetingUid, String sourceWindowUid, Date timeStamp){
		this.meetingUid = meetingUid;
		this.sourceWindowUid = sourceWindowUid;
		this.timeStamp = timeStamp;
	}

	protected String getMeetingUid(){
		return this.meetingUid;
	}
	
	protected String getSourceWindowUid(){
		return this.sourceWindowUid;
	}
	
	@Override
	public Date getTimestamp(){
		return timeStamp;
	}

	@Override
	public void invoke(IChatletApplication application, IWindow window, IEventErrorCallback handler){
		log.debug(String.format("Start processting %s meeting(%s), sourceWindow(%s), for window(%s)", this.getClass(), meetingUid, sourceWindowUid, targetWindowUid));
		
		try{
			if(window == null){
				throw new IdentifierNotExistingException(UidType.WINDOW, targetWindowUid);
			}
			
			IMeeting meeting = window.getMeeting();
			if(meeting == null){
				throw new NoMeetingException(String.format("No meeting existing(%s)", meetingUid), window.getUid());
			}
			if(!meetingUid.equals(meeting.getUid())){
				throw new WindowInOtherMeetingException(String.format("Expect meeting(%s):existing meeting(%s), sourceWindow(%s), for window(%s)", meetingUid, meeting.getUid(), sourceWindowUid, window.getUid()), meetingUid, window.getUid());
			}
			if(application.getMeetingEventListener() == null){
				throw new NoMeetingListenerException(String.format("sourceWindow(%s), for window(%s)",  sourceWindowUid, window.getUid()));
			}
			
			executeTask(application, window);
		}catch(Exception exception){
			try {
				ErrorHandlerInvocation request = new ErrorHandlerInvocation(handler, exception, timeStamp, window.getUid());
				application.getClusterInvocationDistributor().distributeRequest(null, request, handler.getSenderIdType(), handler.getEventSenderUid());
			} catch (IdentifierNotExistingException e) {
				log.warn("Cannot send error back to the original event sender", e);
			}
		}
		
		log.debug(String.format("Done processting %s meeting(%s), sourceWindow(%s), for window(%s)", this.getClass(), meetingUid, sourceWindowUid, window.getUid()));
	}
	
	abstract protected void executeTask(IChatletApplication application, IWindow window);
	
}
