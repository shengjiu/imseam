package com.imseam.raptor.cluster.invocation;

import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.WindowInOtherMeetingException;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.WindowContext;
import com.imseam.raptor.cluster.IClusterInvocation;
import com.imseam.raptor.cluster.invocation.exception.MeetingNotExistingException;

public class BuddyAddedToMeetingInvocation implements IClusterInvocation<IConnection>, IStartActiveWindowCallback{
	
	private static Log log = LogFactory.getLog(BuddyAddedToMeetingInvocation.class);

	private static final long serialVersionUID = -8858611989428898564L;
	private String buddyUid = null;
	private String meetingUid = null;
	private String sourceWindowUid = null;
	private IChatletApplication application = null;
	private Date timeStamp = null;
	private IEventErrorCallback errorCallBack = null; 
	
	
	public BuddyAddedToMeetingInvocation(String meetingUid, String buddyUid, String sourceWindowUid, Date timeStamp){
		this.meetingUid = meetingUid;
		this.sourceWindowUid = sourceWindowUid;
		this.buddyUid = buddyUid;
		this.timeStamp = timeStamp;
	}
	
	@Override
	public Date getTimestamp() {
		return timeStamp;
	}
	
	@Override
	public void invoke(IChatletApplication application, IConnection connection, IEventErrorCallback handler){
		log.debug(String.format("Start processing %s meeting(%s), sourceWindow(%s), for connection(%s)", this.getClass(), meetingUid, sourceWindowUid, connection.getUid()));
		try{
			this.application = application;
			this.errorCallBack = handler;
			Set<IWindow> activeWindowSet = connection.getBuddyActiveWindowSet(buddyUid);
			
			if(activeWindowSet != null){
				for(IWindow window : activeWindowSet){
					IMeeting meeting = application.getMeetingStorage().getExistingMeeting(meetingUid);
					if (meeting != null){
						try{
							((WindowContext)window).setMeeting(meeting);
						}catch(WindowInOtherMeetingException windowInOtherMeetingException){
							continue;
						}
						WindowAddedToMeetingInvocation request = new WindowAddedToMeetingInvocation(meetingUid, window.getUid(), sourceWindowUid, timeStamp);		
						application.getClusterInvocationDistributor().distributeWindowRequest(handler, request, window.getUid());
						return;
					}else{
						//todo no meeting existing
						InvocationErrorHandler.sendExceptionBack(application, new MeetingNotExistingException(meetingUid), handler, timeStamp, sourceWindowUid, buddyUid);
					}
				}
			}
			
			log.info("Starting new window for meeting");
			
			if(application.getMeetingEventListener().beforeStartActiveWindow(connection, buddyUid)){
				connection.startActiveWindow(buddyUid, this);
			}
			
		}catch(Exception exception){
			log.warn("Exception when invite window to meeting.", exception);
			InvocationErrorHandler.sendExceptionBack(application, exception, handler, timeStamp, sourceWindowUid, buddyUid);
		}
	}
	
//	private void addWindowToMeeting(IChatletApplication application, String windowUid, String meetingUid) throws Exception{
//		
//	
//
//		((WindowContext)window).setMeeting(meeting);
//		application.getMeetingEventListener().onJoinedMeeting(window, sourceWindowUid);
//		OtherWindowAddedToMeetingInvocation invocation = new OtherWindowAddedToMeetingInvocation(meetingUid, sourceWindowUid, window.getUid(), new Date());
//		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meetingUid);
//		try {
//			application.getClusterInvocationDistributor().distributeWindowRequest(null, invocation, windowUidSet.toArray(new String[windowUidSet.size()]));
//		} catch (IdentifierNotExistingException e) {
//			log.warn("Error when trying to send window added to meeting", e);
//		}
//		application.getMeetingStorage().addWindowsToMeeting(meetingUid, window.getUid());
//	}
	
	
	@Override
	public void windowStarted(IChannel channel) {
		try {
			IMeeting meeting = application.getMeetingStorage().getExistingMeeting(meetingUid);
			try{
				((WindowContext)channel.getWindow()).setMeeting(meeting);
			}catch(WindowInOtherMeetingException windowInOtherMeetingException){
				ExceptionUtil.createRuntimeException("Shouldn't happen");
			}
			String windowUid = channel.getWindow().getUid();
			
			WindowAddedToMeetingInvocation request = new WindowAddedToMeetingInvocation(meetingUid, windowUid, sourceWindowUid, timeStamp);		
			application.getClusterInvocationDistributor().distributeWindowRequest(errorCallBack, request, windowUid);
		} catch (Exception cause) {
			InvocationErrorHandler.sendExceptionBack(application, cause, errorCallBack, timeStamp, sourceWindowUid, buddyUid);
		}
	}


	@Override
	public void startWindowFailed(Exception cause) {
		InvocationErrorHandler.sendExceptionBack(application, cause, errorCallBack, timeStamp, sourceWindowUid, buddyUid);
	}
	
}