package com.imseam.raptor.cluster.invocation;

import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.WindowContext;

public class WindowRemovedFromMeetingInvocation  extends AbstractInMeetingInvocation{

	private static final long serialVersionUID = 1844792256878985244L;
	private static Log log = LogFactory.getLog(WindowRemovedFromMeetingInvocation.class);
	
	public WindowRemovedFromMeetingInvocation(Date timeStamp, String meetingUid, String sourceWindowUid){
		super(meetingUid, sourceWindowUid, timeStamp);
	}

	@Override
	public void executeTask(IChatletApplication application, IWindow window){
		
		application.getMeetingEventListener().onKickedoutFromMeeting(window, getSourceWindowUid(), getMeetingUid());
		
		application.getMeetingStorage().removeWindowsFromMeeting(this.getMeetingUid(), window.getUid());
		
		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(this.getMeetingUid());
		if(windowUidSet != null && windowUidSet.size() > 0){
			OtherWindowLeftMeetingInvocation request = new OtherWindowLeftMeetingInvocation(this.getMeetingUid(), this.getSourceWindowUid(), window.getUid(), new Date());
			try {
				application.getClusterInvocationDistributor().distributeWindowRequest(null, request, windowUidSet.toArray(new String[windowUidSet.size()]));
			} catch (IdentifierNotExistingException e) {
				log.warn("Invalid identifier", e);
			}
		}
		((WindowContext)window).resetMeeting();
		
	}

}
