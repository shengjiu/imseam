package com.imseam.raptor.cluster.invocation;

import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IClusterInvocation;

public class WindowAddedToMeetingInvocation implements IClusterInvocation<IWindow> {

	private Log log = LogFactory.getLog(WindowAddedToMeetingInvocation.class);

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
		log.debug(String.format("Start processing %s meeting(%s), sourceWindow(%s), for window(%s)", this.getClass(), meetingUid, sourceWindowUid, window.getUid()));

		if (window.getMeeting() != null && window.getMeeting().getUid().equals(meetingUid)) {
			log.warn("Exception when invite window to meeting, and meeting already existing: " + window.getMeeting().getUid());
			InvocationErrorHandler.sendExceptionBack(application, null, handler, timeStamp, sourceWindowUid, buddyUid);
		}

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

}