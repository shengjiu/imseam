package com.imseam.raptor.cluster.invocation;

import java.util.Date;
import java.util.Set;

import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.exception.WindowInOtherMeetingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.WindowContext;
import com.imseam.raptor.cluster.IClusterInvocation;
import com.imseam.raptor.cluster.invocation.exception.BeforeStartMeetingFailedException;
import com.imseam.raptor.cluster.invocation.exception.MeetingNotExistingException;

public class WindowAddedToMeetingInvocation implements IClusterInvocation<IWindow> {

//	private Log log = LogFactory.getLog(WindowAddedToMeetingInvocation.class);

	private static final long serialVersionUID = -8858611989428898564L;
	private String buddyUid = null;
	private String meetingUid = null;
	private String sourceWindowUid = null;
	private Date timeStamp = null;

	public WindowAddedToMeetingInvocation(String meetingUid, String windowUid, String sourceWindowUid, Date timeStamp) {
		this.meetingUid = meetingUid;
		this.sourceWindowUid = sourceWindowUid;
		this.timeStamp = timeStamp;
	}

	@Override
	public Date getTimestamp() {
		return timeStamp;
	}

	@Override
	public void invoke(IChatletApplication application, IWindow window, IEventErrorCallback handler) {
//		log.debug(String.format("Start processing %s meeting(%s), sourceWindow(%s), for window(%s)", this.getClass(), meetingUid, sourceWindowUid, window.getUid()));
		

		IMeeting meeting = application.getMeetingStorage().getExistingMeeting(meetingUid);
		if (meeting == null){
//			log.warn("Exception when invite window to meeting, and meeting already existing: " + window.getMeeting().getUid());
			InvocationErrorHandler.sendExceptionBack(application, new MeetingNotExistingException(meetingUid), handler, timeStamp, sourceWindowUid, buddyUid);
		}
		if(application.getMeetingEventListener().beforeInviteWindow(window) ) {
			InvocationErrorHandler.sendExceptionBack(application, new BeforeStartMeetingFailedException(meetingUid), handler, timeStamp, sourceWindowUid, buddyUid);
		}
		if (window.getMeeting() != null && !window.getMeeting().getUid().equals(meetingUid)) {
//			log.warn("Exception when invite window to meeting, and meeting already existing: " + window.getMeeting().getUid());
		}
		
		try{
			((WindowContext)window).setMeeting(meeting);
		}catch(WindowInOtherMeetingException windowInOtherMeetingException){
			InvocationErrorHandler.sendExceptionBack(application, windowInOtherMeetingException, handler, timeStamp, sourceWindowUid, buddyUid);
		}
		
		Set<String> existingWindowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meetingUid);
		
		
		application.getMeetingStorage().addWindowsToMeeting(meetingUid, window.getUid());
		
		Set<String> existingSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meetingUid);
		String existingWindowUids = "";
		for(String existing : existingSet){
			existingWindowUids += ", " + existing;
		}
		if(!existingSet.contains(window.getUid())){
			System.out.println(String.format("Add windows to meeting error: windowUid(%s) is not added: %s", window.getUid(), existingWindowUids));
		}
		
		application.getMeetingEventListener().onJoinedMeeting(window, sourceWindowUid);
		
		OtherWindowAddedToMeetingInvocation invocation = new OtherWindowAddedToMeetingInvocation(meetingUid, sourceWindowUid, window.getUid(), new Date());
		
		try {
			application.getClusterInvocationDistributor().distributeWindowRequest(null, invocation, existingWindowUidSet.toArray(new String[existingWindowUidSet.size()]));
		} catch (IdentifierNotExistingException e) {
//			log.warn("Error when trying to send window added to meeting", e);
		}
		

	}

}