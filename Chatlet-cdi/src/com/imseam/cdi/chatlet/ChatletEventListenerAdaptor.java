package com.imseam.cdi.chatlet;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.el.ELContextListener;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.Container;
import org.jbpm.JbpmContext;

import com.imseam.cdi.chatlet.annotations.AbstractChatletEventAnnotation;
import com.imseam.cdi.chatlet.annotations.BeforeInviteWindowAnnotation;
import com.imseam.cdi.chatlet.annotations.BuddySignOffAnnotation;
import com.imseam.cdi.chatlet.annotations.BuddyStatusChangeAnnotation;
import com.imseam.cdi.chatlet.annotations.JoinedMeetingAnnotation;
import com.imseam.cdi.chatlet.annotations.KickedoutFromMeetingAnnotation;
import com.imseam.cdi.chatlet.annotations.MeetingStoppedAnnotation;
import com.imseam.cdi.chatlet.annotations.OtherWindowJoinedMeetingAnnotation;
import com.imseam.cdi.chatlet.annotations.OtherWindowLeftMeetingAnnotation;
import com.imseam.cdi.chatlet.annotations.ReceivedMeetingEventAnnotation;
import com.imseam.cdi.chatlet.annotations.ReceivedWindowEventAnnotation;
import com.imseam.cdi.chatlet.annotations.SessionStoppedAnnotation;
import com.imseam.cdi.chatlet.annotations.UserJoinWindowAnnotation;
import com.imseam.cdi.chatlet.annotations.UserLeaveWindowAnnotation;
import com.imseam.cdi.chatlet.annotations.WindowStoppedAnnotation;
import com.imseam.cdi.chatlet.components.ChatPageMapper;
import com.imseam.cdi.chatlet.event.ConnectionRequest;
import com.imseam.cdi.chatlet.event.MeetingEvent;
import com.imseam.cdi.chatlet.ext.annotation.ApplicationInitialized;
import com.imseam.cdi.chatlet.ext.annotation.BeforeApplicationDestroyed;
import com.imseam.cdi.chatlet.ext.annotation.BuddyAdded;
import com.imseam.cdi.chatlet.ext.annotation.BuddyRemoved;
import com.imseam.cdi.chatlet.ext.annotation.BuddySignIn;
import com.imseam.cdi.chatlet.ext.annotation.ConnectionStarted;
import com.imseam.cdi.chatlet.ext.annotation.ConnectionStopped;
import com.imseam.cdi.chatlet.ext.annotation.SessionStarted;
import com.imseam.cdi.chatlet.ext.annotation.UserRequest;
import com.imseam.cdi.chatlet.ext.annotation.WindowStarted;
import com.imseam.cdi.chatlet.ext.annotation.meeting.BeforeStartActiveWindow;
import com.imseam.cdi.chatlet.ext.annotation.meeting.ExtendedMeetingListener;
import com.imseam.cdi.chatlet.services.ChatletServices;
import com.imseam.cdi.chatlet.spi.CDIExtendableMeetingEventListener;
import com.imseam.cdi.weld.WeldEngineHelper;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IChatlet;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.BuddyNotAvailableForChatException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.chatlet.listener.IMeetingEventListener;
import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.chatpage.pageflow.JbpmManager;
import com.imseam.common.util.ClassUtil;

public class ChatletEventListenerAdaptor implements IMeetingEventListener, ISystemEventListener, IChatlet {

	private static final Log log = LogFactory.getLog(ChatletEventListenerAdaptor.class);

	private static final String NOT_STARTED_ERROR = "Weld container is not started!";

	private static final String ILLEGAL_USE_OF_WELD_LISTENER_ERROR = "Illegal use of the weld listener error!";

	private ChatletLifecycle lifecycle;

	private ChatletWeldEngine weldEngine;

	private static ChatletEventListenerAdaptor instance = new ChatletEventListenerAdaptor();

	// private Map<String, Annotation> classNameToAnnotationCache = new
	// HashMap<String, Annotation>();

	private ChatletEventListenerAdaptor() {
	}

	private ChatletLifecycle getLifecycle() {
		if (lifecycle == null) {
			this.lifecycle = Container.instance().services().get(ChatletLifecycle.class);
		}
		return lifecycle;
	}

	public static ChatletEventListenerAdaptor instance() {
		if (instance == null) {
			throw new RuntimeException("The ChatletEventListener should be intializaed already");
		}

		return instance;
	}

	@Override
	public void initialize(Object source, Map<String, String> params) {
		if (weldEngine == null) {
			weldEngine = new ChatletWeldEngine();
		}
	}

	private <T> T getInstanceFromWeldEngine(Class<T> type, Annotation... annotations) {
		return WeldEngineHelper.getInstance().getInstanceFromWeldEngine(type, annotations);
	}

	@Override
	public void onApplicationInitialized(ApplicationEvent appEvent) {
		weldEngine.initialize(appEvent.getApplication());
		WeldEngineHelper.getInstance().initApplication(appEvent.getApplication());
		checkWeldContainer();
		getLifecycle().beginApplicationEvent(appEvent);
		try {
			ChatPageManager.getInstance().initChatPages();
			ChatPageManager.getInstance().addELResolver(weldEngine.getManager().getELResolver());
			ChatPageManager.getInstance().addELContextListener(ClassUtil.<ELContextListener> createInstance("org.jboss.weld.el.WeldELContextListener"));

			this.fireEvent(appEvent, new AnnotationLiteral<ApplicationInitialized>() {
			});
		} finally {
			getLifecycle().endApplicationEvent(appEvent);
		}
	}

	@Override
	public void onApplicationStopped(ApplicationEvent appEvent) {
		checkWeldContainer();
		getLifecycle().beginApplicationEvent(appEvent);
		try {
			this.fireEvent(appEvent, new AnnotationLiteral<BeforeApplicationDestroyed>() {
			});
		} finally {
			getLifecycle().endApplicationEvent(appEvent);
		}
		weldEngine.applicationStopped(appEvent.getApplication());
	}

	private void checkWeldContainer() {
		if (!Container.available()) {
			throw new IllegalStateException(NOT_STARTED_ERROR);
		}
		if (!Container.instance().services().contains(ChatletServices.class)) {
			throw new IllegalStateException(ILLEGAL_USE_OF_WELD_LISTENER_ERROR);
		}
	}

	private void fireEvent(Object event, Annotation annotation) {
		try {
			weldEngine.getManager().fireEvent(event, annotation);
		} catch (RuntimeException rExp) {
			rExp.printStackTrace();
			throw rExp;
		}
	}

	@Override
	public void onBuddyAdded(BuddyEvent event) {
		checkWeldContainer();
		getLifecycle().buddyEventInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			this.fireEvent(event, new AnnotationLiteral<BuddyAdded>() {
			});
		} finally {
			getLifecycle().buddyEventDestroyed(event);
			eventContext.release();
		}
	}

	@Override
	public void onBuddyRemoved(BuddyEvent event) {
		checkWeldContainer();
		getLifecycle().buddyEventInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			this.fireEvent(event, new AnnotationLiteral<BuddyRemoved>() {
			});
		} finally {
			getLifecycle().buddyEventDestroyed(event);
			eventContext.release();
		}
	}

	@Override
	public void onBuddySignIn(BuddyEvent event) {
		checkWeldContainer();
		getLifecycle().buddyEventInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			this.fireEvent(event, new AnnotationLiteral<BuddySignIn>() {
			});
		} finally {
			getLifecycle().buddyEventDestroyed(event);
			eventContext.release();
		}
	}

	@Override
	public void onBuddySignOff(BuddyEvent event) {
		checkWeldContainer();

		getLifecycle().buddyEventInitialized(event);

		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new BuddySignOffAnnotation();
			annotation.setChatflowAndState(null, null);
			this.fireEvent(event, annotation);
		} finally {
			getLifecycle().buddyEventDestroyed(event);
			eventContext.release();
		}

	}

	public void onBuddySignOff(IWindow window, BuddyEvent event) {
		checkWeldContainer();

		getLifecycle().buddyStatusChangeInitialized(window, event);

		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new BuddySignOffAnnotation();
			this.fireChatflowEvent(event, annotation);
			window.getMessageSender().flush();
		} finally {
			getLifecycle().buddyStatusChangeDestroyed(window, event);
			eventContext.release();
		}
	}

	@Override
	public void onBuddyStatusChange(BuddyEvent event) {
		checkWeldContainer();

		getLifecycle().buddyEventInitialized(event);

		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new BuddyStatusChangeAnnotation();
			annotation.setChatflowAndState("null", "null");
			this.fireEvent(event, annotation);
		} finally {
			getLifecycle().buddyEventDestroyed(event);
			eventContext.release();
		}
	}

	public void onBuddyStatusChange(IWindow window, BuddyEvent event) {
		checkWeldContainer();
		getLifecycle().buddyStatusChangeInitialized(window, event);

		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new BuddyStatusChangeAnnotation();
			this.fireChatflowEvent(event, annotation);
			window.getMessageSender().flush();
		} finally {
			getLifecycle().buddyStatusChangeDestroyed(window, event);
			eventContext.release();
		}
	}

	@Override
	public void onConnectionStarted(ConnectionEvent event) {
		checkWeldContainer();
		getLifecycle().connectionInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			this.fireEvent(event, new AnnotationLiteral<ConnectionStarted>() {
			});
		} finally {
			getLifecycle().connectionInitializedEventDone(event);
			eventContext.release();
		}
	}

	@Override
	public void onConnectionStopped(ConnectionEvent event) {
		checkWeldContainer();
		getLifecycle().connectionDestroyed(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			this.fireEvent(event, new AnnotationLiteral<ConnectionStopped>() {
			});
		} finally {
			getLifecycle().connectionDestoryedEventDone(event);
			eventContext.release();
		}
	}

	@Override
	public void onWindowStarted(final WindowEvent event) {
		checkWeldContainer();
		getLifecycle().windowInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, event.getWindow());
			this.fireEvent(event, new AnnotationLiteral<WindowStarted>() {
			});
			event.getWindow().getMessageSender().flush();
		} finally {
			getLifecycle().windowInitializedEventDone(event);
			eventContext.release();
		}
	}

	@Override
	public void onWindowStopped(WindowEvent event) {
		checkWeldContainer();
		getLifecycle().windowDestroyed(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new WindowStoppedAnnotation();
			this.fireChatflowEvent(event, annotation);

		} finally {
			getLifecycle().windowDestroyedEventDone(event);
			eventContext.release();
		}
	}

	@Override
	public void onWindowEventReceived(IWindow window, IEvent event) {
		checkWeldContainer();

		getLifecycle().windowEventRecievedInitialized(window, event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window.getDefaultChannel());
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new ReceivedWindowEventAnnotation();
			this.fireChatflowEvent(event, annotation);
			window.getMessageSender().flush();

		} finally {
			getLifecycle().windowEventRecievedDestroyed(window, event);
			eventContext.release();
		}

	}

	private void fireChatflowEvent(Object event, AbstractChatletEventAnnotation chatflowAnnotation) {
		
		try {
			if(getChatflowRequestProcessor() != null && getChatflowRequestProcessor().isInProcess()){
				String chatflow = getChatflowRequestProcessor().getChatflowName();
				String state = getChatflowRequestProcessor().getState();
	
				chatflowAnnotation.setChatflowAndState(chatflow, state);
				this.fireEvent(event, chatflowAnnotation);
	
				chatflowAnnotation.setChatflowAndState("*", state);
				this.fireEvent(event, chatflowAnnotation);
	
				chatflowAnnotation.setChatflowAndState(chatflow, "*");
				this.fireEvent(event, chatflowAnnotation);
			}

			chatflowAnnotation.setChatflowAndState("*", "*");
			this.fireEvent(event, chatflowAnnotation);

		} catch (RuntimeException rExp) {
			rExp.printStackTrace();
			throw rExp;
		}
	}

	// private <T extends Annotation> ChatflowAnnotationLiteral<T>
	// getQualifier(Class<T> t){
	// String chatflow = getChatflowRequestProcessor().getChatflowName();
	// String state = getChatflowRequestProcessor().getState();
	// return new ChatflowAnnotationLiteral<T>(chatflow, state){};
	// }

	@Override
	public void onUserJoinWindow(UserJoinWindowEvent event) {

		checkWeldContainer();
		getLifecycle().userJoinWindowEventInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, event.getChannel());
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new UserJoinWindowAnnotation();
			this.fireChatflowEvent(event, annotation);

			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().userJoinWindowEventDestroyed(event);
			eventContext.release();
		}

	}

	@Override
	public void onUserLeaveWindow(UserJoinWindowEvent event) {
		checkWeldContainer();
		getLifecycle().userJoinWindowEventInitialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, event.getChannel());
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new UserLeaveWindowAnnotation();
			this.fireChatflowEvent(event, annotation);

			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().userJoinWindowEventDestroyed(event);
			eventContext.release();
		}
	}

	@Override
	public void onSessionStarted(SessionEvent event) {
		checkWeldContainer();
		getLifecycle().sessionIntialized(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);
			this.fireEvent(event, new AnnotationLiteral<SessionStarted>() {
			});
		} finally {
			getLifecycle().sessionIntializedEventDone(event);
			eventContext.release();
		}
	}

	@Override
	public void onSessionStopped(SessionEvent event) {
		checkWeldContainer();
		getLifecycle().sessionDestroyed(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event);

			AbstractChatletEventAnnotation<? extends Annotation> annotation = new SessionStoppedAnnotation();
			this.fireChatflowEvent(event, annotation);
			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());

		} finally {
			getLifecycle().sessionDestroyedEventDone(event);
			eventContext.release();
		}
	}

	private ChatflowRequestProcessor getChatflowRequestProcessor() {
		return ChatflowRequestProcessor.instance();
		// Bean<? extends Object> bean =
		// weldEngine.getManager().resolve(weldEngine.getManager().getBeans(ChatflowRequestProcessor.class));
		// ChatflowRequestProcessor requestProcessor =
		// (ChatflowRequestProcessor) weldEngine.getManager().getReference(bean,
		// ChatflowRequestProcessor.class,
		// weldEngine.getManager().createCreationalContext(bean));
		// return requestProcessor;

	}

	@Override
	public void serviceUserRequest(IUserRequest req, IMessageSender responseSender) {
		checkWeldContainer();

		getLifecycle().beginRequest(req, responseSender);
		EventContext eventContext = null;

		try {
			eventContext = createEventContext(req, req.getRequestFromChannel());
			
			//could be candidates for processor queue

			if (getChatflowRequestProcessor().isInProcess()) {
				
				getChatflowRequestProcessor().processChatRequest(req, responseSender);
				
			} else if(ChatPageMapper.instance().isInProcess()){

				ChatPageMapper.instance().processChatRequest(req, responseSender);
			
			}else{
				this.fireEvent(req, new AnnotationLiteral<UserRequest>() {
				});
			}
				
		} finally {
			getLifecycle().endRequest(req);
			eventContext.release();
		}
	}

	private String getStringInput(IUserRequest chatRequest) {
		if (chatRequest.getRequestContent().getMessageContent() instanceof String) {
			return (String) chatRequest.getRequestContent().getMessageContent();
		} else {
			log.warn("Chat Request type is not supported");
			return null;
		}
	}

	@Override
	public void onKickedoutFromMeeting(IWindow window, String sourceWindowUid, String meetingUid) {
		checkWeldContainer();

		MeetingEvent event = new MeetingEvent(window, sourceWindowUid, meetingUid);

		getLifecycle().beginRequest(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window);

			AbstractChatletEventAnnotation<? extends Annotation> annotation = new KickedoutFromMeetingAnnotation();
			this.fireChatflowEvent(event, annotation);
			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().endRequest(event);
			eventContext.release();
		}
	}

	@Override
	public void onMeetingStopped(IWindow window, String sourceWindowUid) {
		checkWeldContainer();

		MeetingEvent event = new MeetingEvent(window, sourceWindowUid);

		getLifecycle().beginRequest(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new MeetingStoppedAnnotation();
			this.fireChatflowEvent(event, annotation);
			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().endRequest(event);
			eventContext.release();
		}
	}

	private CDIExtendableMeetingEventListener findCDIExtendableMeetingEventListener(Annotation annotation) {
		// get state annotation, which can get from the chatflow configration
		// get event type annotation

		// 1 get an event processor with state annotation and event type
		// annotation
		// 1.1 get an event processor with event type annotation
		// 1.1.1 get an event process with no annotation
		// 2 invoke processEvent
		// 3 the outcome will be sent to the chatflow engine

		// String stateQualifier =
		// requestProcessor.getCurrentChatPage().getAttribute(Constants.CHATFLOW_CONFIGURE_STATE_QUALIFIERS);

		// List<Annotation> annotationList = new ArrayList<Annotation>();
		// annotationList.add(ChatletCDIAnnotation.getAnnotation(annotationClass));
		//
		// if(!StringUtil.isNullOrEmptyAfterTrim(stateQualifier)){
		// String[] qualifiers = stateQualifier.split(",");
		// annotationList.addAll(this.getAnnotationList(qualifiers));
		// }

		CDIExtendableMeetingEventListener eventFromMeetingProcessor = getInstanceFromWeldEngine(CDIExtendableMeetingEventListener.class, annotation);
		if (eventFromMeetingProcessor != null) {
			log.debug(String.format("Extended Meeting Event Listener found (%s)  under the qualifier: %s", eventFromMeetingProcessor.getClass(), annotation));// ,
																																								// stateQualifier));
		} else {
			log.debug(String.format("Extended Meeting Event Listener not found under the qualifier: %s", annotation));
			eventFromMeetingProcessor = getInstanceFromWeldEngine(CDIExtendableMeetingEventListener.class, ChatletCDIAnnotation.getAnnotation(ExtendedMeetingListener.class));
			if (eventFromMeetingProcessor == null) {
				log.debug("Extended Meeting Event Listener not found");
			} else {
				log.debug(String.format("Extended Meeting Event Listener found (%s)", eventFromMeetingProcessor.getClass()));
			}
		}

		return eventFromMeetingProcessor;

	}

	// private List<Annotation> getAnnotationList(String... qualifiers){
	// List<Annotation> annotationList = new ArrayList<Annotation>(qualifiers ==
	// null ? 0 : qualifiers.length);
	//
	// if(qualifiers != null){
	// for(String qualifier : qualifiers){
	// annotationList.add(ChatletCDIAnnotation.getAnnotation(qualifier.trim()));
	// }
	// }
	// return annotationList;
	// }

	@Override
	public void onEventReceivedInMeeting(IWindow window, IEvent event) {

		checkWeldContainer();
		MeetingEvent meetingEvent = new MeetingEvent(window, event);
		getLifecycle().beginRequest(meetingEvent);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new ReceivedMeetingEventAnnotation();
			this.fireChatflowEvent(event, annotation);
		} finally {
			getLifecycle().endRequest(meetingEvent);
			eventContext.release();
		}
	}

	@Override
	public void onOtherWindowLeftMeeting(IWindow window, String sourceWindowId, String kickoutWindowUid) {
		checkWeldContainer();
		MeetingEvent event = new MeetingEvent(window, sourceWindowId);
		getLifecycle().beginRequest(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window);

			AbstractChatletEventAnnotation<? extends Annotation> annotation = new OtherWindowLeftMeetingAnnotation();
			this.fireChatflowEvent(event, annotation);
			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().endRequest(event);
			eventContext.release();
		}
	}

	@Override
	public void onOtherWindowJoinedMeeting(IWindow window, String sourceWindowId, String newWindowUid) {
		checkWeldContainer();
		MeetingEvent event = new MeetingEvent(window, sourceWindowId);

		getLifecycle().beginRequest(event);
		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window);

			AbstractChatletEventAnnotation<? extends Annotation> annotation = new OtherWindowJoinedMeetingAnnotation();
			this.fireChatflowEvent(event, annotation);
			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().endRequest(event);
			eventContext.release();
		}
	}

	@Override
	public void onJoinedMeeting(IWindow window, String sourceWindowUid) {
		checkWeldContainer();
		MeetingEvent event = new MeetingEvent(window, sourceWindowUid);
		getLifecycle().beginRequest(event);

		EventContext eventContext = null;
		try {
			eventContext = createEventContext(event, window);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new JoinedMeetingAnnotation();
			this.fireChatflowEvent(event, annotation);
			getChatflowRequestProcessor().signalTransition(annotation.getTransitionName());
		} finally {
			getLifecycle().endRequest(event);
			eventContext.release();
		}

	}

	@Override
	public boolean beforeInviteWindow(IWindow window) {
		checkWeldContainer();
		MeetingEvent request = new MeetingEvent(window);
		getLifecycle().beginRequest(request);

		EventContext eventContext = null;
		String chatflow = getChatflowRequestProcessor().getChatflowName();
		String state = getChatflowRequestProcessor().getState();
		boolean result = true;

		try {
			eventContext = createEventContext(request, window);
			AbstractChatletEventAnnotation<? extends Annotation> annotation = new BeforeInviteWindowAnnotation();
			annotation.setChatflowAndState(chatflow, state);
			CDIExtendableMeetingEventListener extendedMeetingEventListener = this.findCDIExtendableMeetingEventListener(annotation);
			if (extendedMeetingEventListener != null) {
				result &= extendedMeetingEventListener.beforeInviteWindow(window);
			}
			annotation.setChatflowAndState("*", state);
			extendedMeetingEventListener = this.findCDIExtendableMeetingEventListener(annotation);
			if (extendedMeetingEventListener != null) {
				result &= extendedMeetingEventListener.beforeInviteWindow(window);
			}
			annotation.setChatflowAndState(chatflow, "*");
			extendedMeetingEventListener = this.findCDIExtendableMeetingEventListener(annotation);
			if (extendedMeetingEventListener != null) {
				result &= extendedMeetingEventListener.beforeInviteWindow(window);
			}
			annotation.setChatflowAndState("*", "*");
			extendedMeetingEventListener = this.findCDIExtendableMeetingEventListener(annotation);
			if (extendedMeetingEventListener != null) {
				result &= extendedMeetingEventListener.beforeInviteWindow(window);
			}
		} finally {
			getLifecycle().endRequest(request);
			eventContext.release();
		}
		return result;
	}

	@Override
	public boolean beforeStartActiveWindow(IConnection connection, String buddyUid) throws BuddyNotAvailableForChatException, StartActiveWindowException {
		checkWeldContainer();
		ConnectionRequest request = new ConnectionRequest(connection, buddyUid);
		getLifecycle().beginRequest(request);

		EventContext eventContext = null;
		boolean result = true;

		try {
			eventContext = createEventContext(request);
			
			
			CDIExtendableMeetingEventListener extendedMeetingEventListener = this.findCDIExtendableMeetingEventListener(new AnnotationLiteral<BeforeStartActiveWindow>() {
			});
			if (extendedMeetingEventListener != null) {
				result &= extendedMeetingEventListener.beforeStartActiveWindow(connection, buddyUid);
			}
		} finally {
			getLifecycle().endRequest(request);
			eventContext.release();
		}
		return true;
	}

	private EventContext createEventContext(IAttributes event, IChannel channel) {
		return new EventContext(event, channel);
	}

	private EventContext createEventContext(IAttributes event, IWindow targetWindow) {
		return new EventContext(event, targetWindow);
	}

	private EventContext createEventContext(IAttributes event) {
		return new EventContext(event);
	}

	private class EventContext {
		private ChatpageContext chatpageContext = null;
		private JbpmContext jbpmContext = null;

		EventContext(IAttributes event, IChannel channel) {
			chatpageContext = new ChatpageContext(event, channel);
			jbpmContext = JbpmManager.getInstance().createPageflowContext();
		}

		EventContext(IAttributes event, IWindow targetWindow) {
			chatpageContext = new ChatpageContext(event, targetWindow);
			jbpmContext = JbpmManager.getInstance().createPageflowContext();
		}

		EventContext(IAttributes event) {
			chatpageContext = new ChatpageContext(event);
			jbpmContext = JbpmManager.getInstance().createPageflowContext();
		}

		void release() {
			chatpageContext.release();
			jbpmContext.close();
		}

	}

}
