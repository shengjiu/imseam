package com.imseam.raptor.chatlet;

import java.util.Set;

import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.raptor.threading.PrioritizedTask;
import com.imseam.raptor.threading.RaptorTaskQueue;

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
		public void fireEvent(final ISystemEventListener listener, final IEvent event){
			BuddyEvent buddyEvent = (BuddyEvent)event;
			listener.onBuddySignOff(buddyEvent);
			Set<IWindow> windowSet = buddyEvent.getConnection().getBuddyActiveWindowSet(buddyEvent.getBuddy().getUid());
			if(windowSet != null){
				for(final IWindow window :windowSet){
					final BuddyEvent copiedBuddyEvent = new BuddyEvent(event.getSource(), buddyEvent.getConnection(), buddyEvent.getBuddy());
					RaptorTaskQueue.getInstance(window.getUid()).addTask(new PrioritizedTask <Object, Long>(){
						@Override
						public Object call() throws Exception {
							listener.onBuddySignOff(window, copiedBuddyEvent);
							return null;
						}

						@Override
						public Long getPriority() {
							return event.getTimestamp().getTime();
						}
					});
				}
			}

		}
		
	},
	BuddyStatusChange {
		public Class<BuddyEvent> getEventObjectClass() {
			return BuddyEvent.class;
		}
		public void fireEvent(final ISystemEventListener listener, final IEvent event){
			BuddyEvent buddyEvent = (BuddyEvent)event;
			listener.onBuddyStatusChange(buddyEvent);
			
			Set<IWindow> windowSet = buddyEvent.getConnection().getBuddyActiveWindowSet(buddyEvent.getBuddy().getUid());
			if(windowSet != null){
				for(final IWindow window :windowSet){
					final BuddyEvent copiedBuddyEvent = new BuddyEvent(event.getSource(), buddyEvent.getConnection(), buddyEvent.getBuddy());
					RaptorTaskQueue.getInstance(window.getUid()).addTask(new PrioritizedTask <Object, Long>(){
						@Override
						public Object call() throws Exception {
							listener.onBuddyStatusChange(window, copiedBuddyEvent);
							return null;
						}

						@Override
						public Long getPriority() {
							return event.getTimestamp().getTime();
						}
					});
				
					
				}
			}
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
		public void fireEvent(final ISystemEventListener listener, final IEvent event){
			WindowEvent windowEvent = (WindowEvent)event;
			RaptorTaskQueue.getInstance(windowEvent.getWindow().getUid()).addTask(new PrioritizedTask <Object, Long>(){
				@Override
				public Object call() throws Exception {
					listener.onWindowStarted((WindowEvent)event);
					return null;
				}

				@Override
				public Long getPriority() {
					return event.getTimestamp().getTime();
				}
			});
		}
	},
	windowStopped {
		public Class<WindowEvent> getEventObjectClass() {
			return WindowEvent.class;
		}
		public void fireEvent(final ISystemEventListener listener, final IEvent event){
			WindowEvent windowEvent = (WindowEvent)event;
			RaptorTaskQueue.getInstance(windowEvent.getWindow().getUid()).addTask(new PrioritizedTask <Object, Long>(){
				@Override
				public Object call() throws Exception {
					listener.onWindowStopped((WindowEvent)event);
					return null;
				}

				@Override
				public Long getPriority() {
					return event.getTimestamp().getTime();
				}
			});
		}
	},
	UserJoinWindow {
		public Class<UserJoinWindowEvent> getEventObjectClass() {
			return UserJoinWindowEvent.class;
		}
		public void fireEvent(final ISystemEventListener listener, final IEvent event){
			UserJoinWindowEvent userJoinWindowEvent = (UserJoinWindowEvent)event;
			RaptorTaskQueue.getInstance(userJoinWindowEvent.getChannel().getWindow().getUid()).addTask(new PrioritizedTask <Object, Long>(){
				@Override
				public Object call() throws Exception {
					listener.onUserJoinWindow((UserJoinWindowEvent)event);
					return null;
				}

				@Override
				public Long getPriority() {
					return event.getTimestamp().getTime();
				}
			});
		}
	},
	UserLeaveWindow {
		public Class<UserJoinWindowEvent> getEventObjectClass() {
			return UserJoinWindowEvent.class;
		}
		public void fireEvent(final ISystemEventListener listener, final IEvent event){
			
			UserJoinWindowEvent userJoinWindowEvent = (UserJoinWindowEvent)event;
			RaptorTaskQueue.getInstance(userJoinWindowEvent.getChannel().getWindow().getUid()).addTask(new PrioritizedTask <Object, Long>(){
				@Override
				public Object call() throws Exception {
					listener.onUserLeaveWindow((UserJoinWindowEvent)event);
					return null;
				}

				@Override
				public Long getPriority() {
					return event.getTimestamp().getTime();
				}
			});
			
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
