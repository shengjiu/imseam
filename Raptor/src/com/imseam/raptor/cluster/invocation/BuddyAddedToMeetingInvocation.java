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
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.WindowContext;
import com.imseam.raptor.cluster.IClusterInvocation;

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
			
			for(IWindow window : activeWindowSet){
				if(window.getMeeting() == null){
					IMeeting meeting = application.getMeetingStorage().getExistingMeeting(meetingUid);
					if (meeting != null && application.getMeetingEventListener().beforeInviteWindow(window) ) {
						
						((WindowContext)window).setMeeting(meeting);
						addWindowToMeeting(application, window);
						return;
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
	
	private void addWindowToMeeting(IChatletApplication application, IWindow window) {

		application.getMeetingEventListener().onJoinedMeeting(window, sourceWindowUid);
		OtherWindowAddedToMeetingInvocation invocation = new OtherWindowAddedToMeetingInvocation(meetingUid, sourceWindowUid, window.getUid(), new Date());
		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meetingUid);
		try {
			application.getClusterInvocationDistributor().distributeWindowRequest(null, invocation, windowUidSet.toArray(new String[windowUidSet.size()]));
		} catch (IdentifierNotExistingException e) {
			log.warn("Error when trying to send window added to meeting", e);
		}
		application.getMeetingStorage().addWindowsToMeeting(meetingUid, window.getUid());
	}
	
	
	@Override
	public void windowStarted(IChannel channel) {
		addWindowToMeeting(application, channel.getWindow());
	}


	@Override
	public void startWindowFailed(Exception cause) {
		InvocationErrorHandler.sendExceptionBack(application, cause, errorCallBack, timeStamp, sourceWindowUid, buddyUid);
	}


	
}