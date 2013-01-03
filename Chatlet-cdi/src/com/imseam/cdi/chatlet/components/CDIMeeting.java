package com.imseam.cdi.chatlet.components;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.Container;

import com.imseam.cdi.chatlet.ChatletLifecycle;
import com.imseam.cdi.chatlet.Id;
import com.imseam.cdi.chatlet.spi.AbstractCDIErrorCallback;
import com.imseam.cdi.chatlet.spi.Func;
import com.imseam.cdi.chatlet.spi.FuncBasedEvent;
import com.imseam.cdi.context.IMWindowScoped;
import com.imseam.chatlet.IContext;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.exception.NoMeetingException;
import com.imseam.chatlet.exception.WindowInOtherMeetingException;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.chatpage.context.ChatpageContext;



@IMWindowScoped
public class CDIMeeting {
	private static Log log = LogFactory.getLog(CDIMeeting.class);
	
	private @Inject Instance<IWindow> window;    
	
	public IMeeting startMeetingWithBuddy(AbstractCDIErrorCallback errorCallback, String... buddyUids) throws WindowInOtherMeetingException, IdentifierNotExistingException{
		if(window.get().getMeeting() != null){
			log.warn("window is already in a meeting, startMeetingWithBuddy cancelled");
			return null;
		}
		IMeeting meeting = window.get().startMeetingWithBuddy(errorCallback, buddyUids);
		setMeetingContext(meeting);
		return meeting;
	}

	public IMeeting startMeetingWithWindow(AbstractCDIErrorCallback errorCallback, String... windowUids) throws WindowInOtherMeetingException, IdentifierNotExistingException{
		if(window.get().getMeeting() != null){
			log.warn("window is already in a meeting, startMeetingWithWindow cancelled");
			return null;
		}
		IMeeting meeting = window.get().startMeetingWithWindow(errorCallback, windowUids);
		setMeetingContext(meeting);
		return meeting;
	}
	
	private void setMeetingContext(IMeeting meeting){
		Container.instance().services().get(ChatletLifecycle.class).meetingCreated(meeting);
	}

	public void leaveMeeting(AbstractCDIErrorCallback errorCallback) throws NoMeetingException{
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, leave meeting cancelled");
			return;
		}

		window.get().leaveMeeting(errorCallback);
	}
	
	public void addBuddyToMeeting(AbstractCDIErrorCallback errorCallback, String... buddyUids) throws NoMeetingException, IdentifierNotExistingException{
		if(window.get().getMeeting() == null){
			log.warn("window is not in a meeting, addBuddyToMeeting cancelled");
			return;
		}

		window.get().addBuddyToMeeting(errorCallback, buddyUids);
	}

	public void addWindowToMeeting(AbstractCDIErrorCallback errorCallback, String... windowUids) throws NoMeetingException, IdentifierNotExistingException{
		
		if(window.get().getMeeting() == null){
			log.warn("window is not in a meeting, addWindowToMeeting cancelled");
			return;
		}
		window.get().addWindowToMeeting(errorCallback, windowUids);
	}

	
	public void kickoutWindowFromMeeting(AbstractCDIErrorCallback errorCallback, String windowUid)throws NoMeetingException, IdentifierNotExistingException{
		if(window.get().getMeeting() == null){
			log.warn("window is not in a meeting, kickoutWindowFromMeeting cancelled");
			return;
		}
		
		window.get().kickoutWindowFromMeeting(errorCallback, windowUid);
	}
	
	public void stopMeeting(AbstractCDIErrorCallback errorCallback) throws NoMeetingException{
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, stopMeeting cancelled");
			return;
		}
		
		window.get().stopMeeting(errorCallback);
	}

	public void fireMeetingEventToAllOtherWindows(IEvent event, AbstractCDIErrorCallback errorCallback)throws NoMeetingException{
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, fireMeetingEventToAllOtherWindows cancelled");
			return;
		}
		
		window.get().fireMeetingEventToAllOtherWindows(event, errorCallback);
	}
	
	public void fireMeetingEventToWindows(IEvent event, AbstractCDIErrorCallback errorCallback, String...windowUids) throws NoMeetingException, IdentifierNotExistingException{
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, fireMeetingEventToWindows cancelled");
			return;
		}

		
		window.get().fireMeetingEventToWindows(event, errorCallback, windowUids);
	}
	
	public boolean isInMeeting(){
		return window.get().getMeeting() != null;
	}
	
	public IMeeting startMeetingWithBuddy(String... buddyUids){
		if(window.get().getMeeting() != null){
			log.warn("window is already in a meeting, startMeeting cancelled");
			return null;
		}

		
		 try {
			return startMeetingWithBuddy(null, buddyUids);
		} catch (WindowInOtherMeetingException e) {
			log.warn(buddyUids, e);
		} catch (IdentifierNotExistingException e) {
			log.warn(buddyUids, e);
		}
		return null;
	}
	
	public void leaveMeeting(){
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, leave meeting cancelled");
			return;
		}

		try {
			leaveMeeting(null);
		} catch (NoMeetingException e) {
			log.warn(e);
		}
	}
	
	public boolean addBuddyToMeeting(String... buddyUids){
		if(window.get().getMeeting() == null){
			log.warn("window is not in a meeting, add buddy to Meeting cancelled");
			return false;
		}

		try {
			addBuddyToMeeting(null, buddyUids);
		} catch (Exception e) {
			log.warn(e);
			return false;
		} 
		return true;
	}
	
	public void stopMeeting(){
		try {
			stopMeeting(null);
		} catch (NoMeetingException e) {
			log.warn(e);
		}		
	}
	
	public IMeeting startMeetingWithWindow(String... windowUids){
		 try {
			//return startMeetingWithWindow(LogCDIErrorCallback.of(window.get()), windowUids);
			return startMeetingWithWindow(null, windowUids);
		} catch (WindowInOtherMeetingException e) {
			log.warn(windowUids, e);
		} catch (IdentifierNotExistingException e) {
			log.warn(windowUids, e);
		}
		return null;
	}
	
	public boolean addWindowToMeeting(String... windowUids){
		try {
			addWindowToMeeting(null, windowUids);
		} catch (Exception e) {
			log.warn(e);
			return false;
		} 
		return true;
	}	
	
	public void joinMeeting(String meetingUid){
		try {
			window.get().joinMeeting(meetingUid);
		} catch (WindowInOtherMeetingException e) {
			log.warn(e);
		} catch (IdentifierNotExistingException e) {
			log.warn(e);
		}
	}

	public void invoke(Func func, String... windowUids){
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, invoke func cancelled");
			return;
		}

		
		IEvent event = new FuncBasedEvent(func);
		
		if(windowUids == null || windowUids.length == 0){
			try {
				window.get().fireMeetingEventToAllOtherWindows(event, null);
			} catch (NoMeetingException e) {
				log.warn("no meeting started for window yet:" + window.get().getUid(), e);
			}
		}else{
			try {
				window.get().fireMeetingEventToWindows(event, null, windowUids);
			} catch (NoMeetingException e) {
				log.warn("no meeting started for window yet:" + window.get().getUid(), e);
			} catch (IdentifierNotExistingException e) {
				log.warn("Identifier not existing", e);
			}
		}
		
		
	}
	
	private static Func createSendMessageFunction(String windowUid, final String message){
		return new Func(Id.windowUid(windowUid)){

			private static final long serialVersionUID = 1L;

			@Override
			public void invoke(IContext context) {
				assert(context != null);
				assert(context instanceof IWindow);
				assert(ChatpageContext.current().getWindow() == context);
				
				ChatpageContext.current().sendELString(message);
				
			}
			
		};

	}

	public void send(final String message, String...windowUids){
		if(window.get().getMeeting() == null){
			log.trace("window is not in a meeting, send message cancelled");
			return;
		}
		
		invoke(createSendMessageFunction(window.get().getUid(), message), windowUids);
	}
	

}
