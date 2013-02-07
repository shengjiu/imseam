package com.imseam.raptor.chatlet;

import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;

public enum EventTypeEnum {
	
	ApplicationInitialized {
		public Class<ApplicationEvent> getEventObjectClass() {
			return ApplicationEvent.class;
		}
		
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onApplicationInitialized((ApplicationEvent)event);
		}
	},
	ApplicationStopped {
		public Class<ApplicationEvent> getEventObjectClass() {
			return ApplicationEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onApplicationStopped((ApplicationEvent)event);
		}
	},
	
	SessionStarted {
		public Class<SessionEvent> getEventObjectClass() {
			return SessionEvent.class;
		}
		
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onSessionStarted((SessionEvent)event);
		}
	},
	SessionStopped {
		public Class<SessionEvent> getEventObjectClass() {
			return SessionEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onSessionStopped((SessionEvent)event);
		}
	},	
	BuddyAdded {
		public Class<BuddyEvent> getEventObjectClass() {
			return BuddyEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onBuddyAdded((BuddyEvent)event);
		}
		
	},
	BuddyRemoved {
		public Class<BuddyEvent> getEventObjectClass() {
			return BuddyEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onBuddyRemoved((BuddyEvent)event);
		}
	},
	BuddySignIn {
		public Class<BuddyEvent> getEventObjectClass() {
			return BuddyEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onBuddySignIn((BuddyEvent)event);
		}
	},
	BuddySignOff {
		public Class<BuddyEvent> getEventObjectClass() {
			return BuddyEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onBuddySignOff((BuddyEvent)event);
		}
		
	},
	BuddyStatusChange {
		public Class<BuddyEvent> getEventObjectClass() {
			return BuddyEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onBuddyStatusChange((BuddyEvent)event);
		}
	},
	ConnectionStarted {
		public Class<ConnectionEvent> getEventObjectClass() {
			return ConnectionEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onConnectionStarted((ConnectionEvent)event);
		}
	},
	ConnectionStopped {
		public Class<ConnectionEvent> getEventObjectClass() {
			return ConnectionEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onConnectionStopped((ConnectionEvent)event);
		}
	},
	WindowStarted {
		public Class<WindowEvent> getEventObjectClass() {
			return WindowEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onWindowStarted((WindowEvent)event);
		}
	},
	windowStopped {
		public Class<WindowEvent> getEventObjectClass() {
			return WindowEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onWindowStopped((WindowEvent)event);
		}
	},
	UserJoinWindow {
		public Class<UserJoinWindowEvent> getEventObjectClass() {
			return UserJoinWindowEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onUserJoinWindow((UserJoinWindowEvent)event);
		}
	},
	UserLeaveWindow {
		public Class<UserJoinWindowEvent> getEventObjectClass() {
			return UserJoinWindowEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			listener.onUserLeaveWindow((UserJoinWindowEvent)event);
		}
	},
	
	WindowEventRecieved {
		public Class<IEvent> getEventObjectClass() {
			return IEvent.class;
		}
		public void fireEvent(ISystemEventListener listener, IEvent event){
			ToWindowEventWrapper wrappedEvent = (ToWindowEventWrapper)event;
			listener.onWindowEventReceived(wrappedEvent.getWindow(), wrappedEvent.getEvent());
		}
	};

	
//	BeforeKickedoutFromMeeting {
//		public Class<WindowEvent> getEventObjectClass() {
//			return WindowEvent.class;
//		}
//		public void fireEvent(IEventListener listener, EventObject event){
//			listener.onBeforeKickoutFromMeeting((WindowEvent)event);
//		}
//	},
//	KickedoutFromMeeting {
//		public Class<WindowEvent> getEventObjectClass() {
//			return WindowEvent.class;
//		}
//		public void fireEvent(IEventListener listener, EventObject event){
//			listener.onKickedoutFromMeeting((WindowEvent)event);
//		}
//	},
//	BeforeMeetingStopping {
//		public Class<WindowEvent> getEventObjectClass() {
//			return WindowEvent.class;
//		}
//		public void fireEvent(IEventListener listener, EventObject event){
//			listener.onBeforeMeetingStopping((WindowEvent)event);
//		}
//	},
//	MeetingStopped {
//		public Class<WindowEvent> getEventObjectClass() {
//			return WindowEvent.class;
//		}
//		public void fireEvent(IEventListener listener, EventObject event){
//			listener.onMeetingStopped((WindowEvent)event);
//		}
//	};

	public abstract Class<? extends IEvent> getEventObjectClass();
	
	public abstract void fireEvent(ISystemEventListener listener, IEvent event);
}
