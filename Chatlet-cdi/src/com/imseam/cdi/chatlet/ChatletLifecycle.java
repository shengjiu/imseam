package com.imseam.cdi.chatlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.Container;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.context.AbstractBoundContext;

import com.imseam.cdi.chatlet.components.CDIRequestObjectInThreadHolder;
import com.imseam.cdi.chatlet.event.ConnectionErrorCallbackRequest;
import com.imseam.cdi.chatlet.event.ConnectionRequest;
import com.imseam.cdi.chatlet.event.FromMeetingRequest;
import com.imseam.cdi.chatlet.event.WindowErrorCallbackRequest;
import com.imseam.cdi.context.IMChannelContext;
import com.imseam.cdi.context.IMConnectionContext;
import com.imseam.cdi.context.IMMeetingContext;
import com.imseam.cdi.context.IMRequestContext;
import com.imseam.cdi.context.IMSessionContext;
import com.imseam.cdi.context.IMWindowContext;
import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;

public class ChatletLifecycle implements Service {

	private static final Log log = LogFactory.getLog(ChatletLifecycle.class);

//	private static class RequestBeanStoreCache {
//		private static final String REQUEST_ATTRIBUTE_NAME = ChatletLifecycle.class.getName() + ".requestBeanStore";
//
//		public static void clear(IAttributes request) {
//			request.removeAttribute(REQUEST_ATTRIBUTE_NAME);
//		}
//
//		public static BeanStore get(IAttributes request) {
//			return (BeanStore) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
//		}
//
//		public static void set(IAttributes request, BeanStore requestBeanStore) {
//			request.setAttribute(REQUEST_ATTRIBUTE_NAME, requestBeanStore);
//		}
//
//		public static boolean isSet(IAttributes request) {
//			return get(request) != null;
//		}
//	}

//	private IMApplicationContext applicationContext;
//	private IMSingletonContext singletonContext;
	private IMSessionContext sessionContextCache;
	private IMRequestContext requestContextCache;
	private IMWindowContext windowContextCache;
	private IMChannelContext channelContextCache;
	private IMMeetingContext meetingContextCache;
	private IMConnectionContext connectionContextCache;
//	private final IMDependentContext dependentContext = new IMDependentContext();
	private IApplication application;

	public ChatletLifecycle(IApplication application) {
		this.application = application;
	}

	public IApplication getApplication() {
		return application;
	}
	
//	private HttpRequestContext requestContext() {
//        if (requestContextCache == null) {
//            this.requestContextCache = Container.instance().deploymentManager().instance().select(HttpRequestContext.class).get();
//        }
//        return requestContextCache;
//    }
//
//    private HttpConversationContext conversationContext() {
//        if (conversationContextCache == null) {
//            this.conversationContextCache = Container.instance().deploymentManager().instance().select(HttpConversationContext.class).get();
//        }
//        return conversationContextCache;
//    }
	
//	public IMSingletonContext getSingletonContext() {
//		return singletonContext;
//	}

	public IMSessionContext sessionContext() {
		if( sessionContextCache == null){
			this.sessionContextCache = Container.instance().deploymentManager().instance().select(IMSessionContext.class).get();
		}
		return sessionContextCache;
	}

	public IMRequestContext requestContext() {
		if( requestContextCache == null){
			this.requestContextCache = Container.instance().deploymentManager().instance().select(IMRequestContext.class).get();
		}
		return requestContextCache;
	}

	public IMWindowContext windowContext() {
		if( windowContextCache == null){
			this.windowContextCache = Container.instance().deploymentManager().instance().select(IMWindowContext.class).get();
		}

		return windowContextCache;
	}

//	public IMApplicationContext getApplicationContext() {
//		return applicationContext;
//	}

	public IMChannelContext channelContext() {
		if( channelContextCache == null){
			this.channelContextCache = Container.instance().deploymentManager().instance().select(IMChannelContext.class).get();
		}		
		return channelContextCache;
	}

	public IMMeetingContext meetingContext() {
		if( meetingContextCache == null){
			this.meetingContextCache = Container.instance().deploymentManager().instance().select(IMMeetingContext.class).get();
		}				
		return meetingContextCache;
	}

	public IMConnectionContext connectionContext() {
		if( connectionContextCache == null){
			this.connectionContextCache = Container.instance().deploymentManager().instance().select(IMConnectionContext.class).get();
		}				
		return connectionContextCache;
	}

//	public IMDependentContext getDependentContext() {
//		return dependentContext;
//	}

//	public boolean isRequestActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && requestContext.isActive() && dependentContext.isActive();
//	}
//
//	public boolean isApplicationActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && dependentContext.isActive();
//	}
//
//	public boolean isWindowActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && windowContext.isActive() && dependentContext.isActive();
//	}
//
//	public boolean isSessionActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && sessionContext.isActive() && dependentContext.isActive();
//	}
//
//	public boolean isChannelActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && channelContext.isActive() && dependentContext.isActive();
//	}
//
//	public boolean isMeetingActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && meetingContext.isActive() && dependentContext.isActive();
//	}
//
//	public boolean isConnecionActive() {
//		return singletonContext.isActive() && applicationContext.isActive() && connectionContext.isActive() && dependentContext.isActive();
//	}

	@Override
	public void cleanup() {
//		dependentContext.cleanup();
		requestContextCache.cleanup();
		sessionContextCache.cleanup();
		windowContextCache.cleanup();
		channelContextCache.cleanup();
		meetingContextCache.cleanup();
		connectionContextCache.cleanup();
//		singletonContext.cleanup();
//		applicationContext.cleanup();
	}

//	public void beginApplication(BeanStore applicationBeanStore) {
//		log.debug(">>> Begin chatlet application");
//		activateContext(applicationContext, applicationBeanStore);
//		activateContext(singletonContext, new ConcurrentHashMapBeanStore());
//	}
//
//	
	public void beginApplicationEvent(ApplicationEvent appEvent) {
		log.debug(">>> Begin chatlet application event");
		activateContext(requestContext(), appEvent);
		CDIRequestObjectInThreadHolder.getInstance().setApplicatonEvent(appEvent);
	}

	public void endApplicationEvent(ApplicationEvent appEvent) {
		destroyContext(requestContext());
	}
//
//	public void endApplication() {
//		log.debug(">>> Chatlet application ended!");
//		destroyContext(applicationContext);
//		destroyContext(singletonContext);
//	}

	private IWindow getWindow(IUserRequest request) {
		return request.getRequestFromChannel().getWindow();
	}
	
	public void beginRequest(FromMeetingRequest request) {
		log.debug(">>> Begin request from meeting for window: " + request.getWindow().getUid());
		
		IWindow window = request.getWindow();
		
		activateContext(requestContext(), request);
		activateContext(sessionContext(), window.getDefaultChannel().getUserSession());
		activateContext(channelContext(), window.getDefaultChannel());
		activateContext(connectionContext(), window.getConnection());
		activateContext(windowContext(), window);
		if(window.getMeeting() != null){
			activateContext(meetingContext(), window.getMeeting());
		}
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(request);
	}
	
	public void endRequest(FromMeetingRequest request) {
		log.debug(">>> End request from meeting for window: " + request.getWindow().getUid());
		IWindow window = request.getWindow();
		destroyContext(requestContext());
		deactivateContext(sessionContext(), window.getDefaultChannel().getUserSession());
		deactivateContext(channelContext(), window.getDefaultChannel());
		deactivateContext(connectionContext(), window.getConnection());
		deactivateContext(windowContext(), window);
		if(window.getMeeting() != null){
			deactivateContext(meetingContext(), window.getMeeting());
		}

	}
	
	public void beginRequest(ConnectionErrorCallbackRequest request) {
		log.debug(">>> Begin error callback request from connection: " + request.getConnection().getUid());

		activateContext(requestContext(), request);
		activateContext(connectionContext(), request.getConnection());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(request);
	}
	
	public void endRequest(ConnectionErrorCallbackRequest request) {
		log.debug(">>> End error callback request from connection: " + request.getConnection().getUid());
		destroyContext(requestContext());
		deactivateContext(connectionContext(), request.getConnection());

	}	

	public void beginRequest(WindowErrorCallbackRequest request) {

		log.debug(">>> Begin error callback from window: " + request.getWindow().getUid());
		
		IWindow window = request.getWindow();

		activateContext(requestContext(), request);
		activateContext(sessionContext(), window.getDefaultChannel().getUserSession());
		activateContext(channelContext(), window.getDefaultChannel());
		activateContext(connectionContext(), window.getConnection());
		activateContext(windowContext(), window);
		if(window.getMeeting() != null){
			activateContext(meetingContext(), window.getMeeting());
		}
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(request);
	}
	
	public void endRequest(WindowErrorCallbackRequest request) {
		log.debug(">>> End error callback from window: " + request.getWindow().getUid());
		IWindow window = request.getWindow();
		destroyContext(requestContext());
		deactivateContext(sessionContext(), window.getDefaultChannel().getUserSession());
		deactivateContext(channelContext(), window.getDefaultChannel());
		deactivateContext(connectionContext(), window.getConnection());
		deactivateContext(windowContext(), window);
		if(window.getMeeting() != null){
			deactivateContext(meetingContext(), window.getMeeting());
		}

	}	
	
//	public void meetingStopped(FromMeetingRequest event) {
//		if (RequestBeanStoreCache.isSet(event)) {
//			return;
//		}
//		log.debug(">>> Begin meeting stopped for window event, window id: " + event.getWindow().getUid());
//		BeanStore requestBeanStore = getBeanStore(event);
//		RequestBeanStoreCache.set(event, requestBeanStore);
//
//		activateDependentContext();
//		activateContext(requestContext, requestBeanStore);
//		activateContext(connectionContext, getBeanStore(event.getWindow().getConnection()));
//		activateContext(windowContext, getBeanStore(event.getWindow()));
//		activateContext(meetingContext, getBeanStore(event.getWindow().getMeeting()));
//	}	

	public void beginRequest(ConnectionRequest request) {
		log.debug(">>> Begin event for connection: " + request.getConnection().getHostUserId());
		
		activateContext(requestContext(), request);
		activateContext(connectionContext(), request.getConnection());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(request);
	}
	
	public void endRequest(ConnectionRequest request) {
		log.debug(">>> End event for connection: " + request.getConnection().getHostUserId());
		destroyContext(requestContext());
		deactivateContext(connectionContext(), request.getConnection());
	}	


	public void beginRequest(IUserRequest request, IMessageSender sender) {
		log.debug(">>> Begin chatlet request: " + request.getUid());
		IWindow window = getWindow(request);
		activateContext(requestContext(), request);
		activateContext(sessionContext(), request.getRequestFromChannel().getUserSession());
		activateContext(channelContext(), request.getRequestFromChannel());
		activateContext(connectionContext(), window.getConnection());
		activateContext(windowContext(), window);
		if(window.getMeeting() != null){
			activateContext(meetingContext(), window.getMeeting());
		}
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(request);
//		CDIRequestObjectInThreadHolder.getInstance().setMessageSender(sender);

	}

	public void endRequest(IUserRequest request) {
		log.debug(">>> End chatlet request: " + request.getUid());
		IWindow window = getWindow(request);
		destroyContext(requestContext());
		deactivateContext(sessionContext(), request.getRequestFromChannel().getUserSession());
		deactivateContext(channelContext(), request.getRequestFromChannel());
		deactivateContext(connectionContext(), window.getConnection());
		deactivateContext(windowContext(), window);
		if(window.getMeeting() != null){
			deactivateContext(meetingContext(), window.getMeeting());
		}
	}
	
//	public <T extends IContext> void beginRequest(T context, AbstractCdiRequestTask<T> request){
//		if (RequestBeanStoreCache.isSet(request)) {
//			return;
//		}
//		log.debug(">>> Begin request task: " + request.getUid());
//
//		BeanStore requestBeanStore = getBeanStore(request);
//		RequestBeanStoreCache.set(request, requestBeanStore);
//		activateDependentContext();
//		activateContext(requestContext, requestBeanStore);
//
//		if(context == null || context instanceof IApplication){
//			//doing nothing, since the application is already started
//		}
//		if(context instanceof IWindow){
//			IWindow window = (IWindow) context;
//			activateContext(connectionContext, getBeanStore(window.getConnection()));
//			activateContext(windowContext, getBeanStore(window));
//			activateContext(channelContext, getBeanStore(window.getDefaultChannel()));
//			activateContext(sessionContext, getBeanStore(window.getDefaultChannel().getUserSession()));
//			if(window.getMeeting() != null){
//				activateContext(meetingContext, getBeanStore(window.getMeeting()));
//			}
//		}
//
//		if(context instanceof IConnection){
//			IConnection connection = (IConnection) context;
//			activateContext(connectionContext, getBeanStore(connection));
//		}
//		
//		if(context instanceof ISession){
//			ISession session = (ISession) context;
//			activateContext(sessionContext, getBeanStore(session));
//		}
//
//		if(context instanceof IChannel){
//			IChannel channel = (IChannel) context;
//			activateContext(connectionContext, getBeanStore(channel.getWindow().getConnection()));
//			activateContext(windowContext, getBeanStore(channel.getWindow()));
//			activateContext(channelContext, getBeanStore(channel));
//			activateContext(sessionContext, getBeanStore(channel.getUserSession()));
//			
//			if(channel.getWindow().getMeeting() != null){
//				activateContext(meetingContext, getBeanStore(channel.getWindow().getMeeting()));
//			}
//		}
//		
//	}
//
//	public <T extends IContext> void endRequest(T context, AbstractCdiRequestTask<T> request){
//		if (!RequestBeanStoreCache.isSet(request)) {
//			return;
//		}
//		log.debug(">>> End request task: " + request.getUid());
//		
//		deactivateDependentContext();
//		destroyContext(requestContext);
//		
//		if(context == null || context instanceof IApplication){
//			//doing nothing, since the application is already started
//		}
//		if(context instanceof IWindow){
//			
//			destroyContext(connectionContext);
//			destroyContext(windowContext);
//			destroyContext(channelContext);
//			destroyContext(sessionContext);
//			destroyContext(meetingContext);
//		}
//
//		if(context instanceof IConnection){
//			destroyContext(connectionContext);
//		}
//		
//		if(context instanceof ISession){
//			destroyContext(sessionContext);
//		}
//
//		if(context instanceof IChannel){
//			destroyContext(connectionContext);
//			destroyContext(windowContext);
//			destroyContext(channelContext);
//			destroyContext(sessionContext);
//			destroyContext(meetingContext);
//		}
//		
//
//		RequestBeanStoreCache.clear(request);
//
//		
//	}
	
	public void userJoinWindowEventInitialized(UserJoinWindowEvent event) {
		log.debug(">>> Begin channel added: " + event.getChannel().getBuddy().getUid());
		activateContext(requestContext(), event);
		activateContext(sessionContext(), event.getChannel().getUserSession());
		activateContext(channelContext(), event.getChannel());
		activateContext(connectionContext(), event.getChannel().getWindow().getConnection());
		activateContext(windowContext(), event.getChannel().getWindow());
		if(event.getChannel().getWindow().getMeeting() != null){		
			activateContext(meetingContext(), event.getChannel().getWindow().getMeeting());
		}
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}

	public void userJoinWindowEventDestroyed(UserJoinWindowEvent event) {
		log.debug(">>> End channel added: " + event.getChannel().getBuddy().getUid());
		destroyContext(requestContext());
		deactivateContext(sessionContext(), event.getChannel().getUserSession());
		deactivateContext(channelContext(), event.getChannel());
		deactivateContext(connectionContext(), event.getChannel().getWindow().getConnection());
		deactivateContext(windowContext(), event.getChannel().getWindow());
		if(event.getChannel().getWindow().getMeeting() != null){		
			deactivateContext(meetingContext(), event.getChannel().getWindow().getMeeting());
		}
	}	
	
	public void sessionIntialized(SessionEvent event) {
		log.debug(">>> Begin session intialize event: " + event.getSession());
		activateContext(requestContext(), event);
		activateContext(sessionContext(), event.getSession());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}

	public void sessionIntializedEventDone(SessionEvent event) {
		log.debug(">>> End session intialize event: " + event.getSession());
		destroyContext(requestContext());
		deactivateContext(sessionContext(), event.getSession());
	}

	
	public void sessionDestroyed(SessionEvent event) {
		log.debug(">>> begin destroy session event: " + event.getSession());
		activateContext(requestContext(), event);
		activateContext(sessionContext(), event.getSession());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}

	public void sessionDestroyedEventDone(SessionEvent event) {

		log.debug(">>> End destroy session event: " + event.getSession());
		destroyContext(requestContext());
		destroyContext(sessionContext());
	}

	
	public void windowInitialized(WindowEvent event) {
		log.debug(">>> Begin window intialize event: " + event.getWindow().getUid());
		activateContext(requestContext(), event);
		activateContext(sessionContext(), event.getWindow().getDefaultChannel().getUserSession());
		activateContext(channelContext(), event.getWindow().getDefaultChannel());
		activateContext(connectionContext(), event.getWindow().getConnection());
		activateContext(windowContext(), event.getWindow());
		if(event.getWindow().getMeeting() != null){
			activateContext(meetingContext(), event.getWindow().getMeeting());
		}
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}	
	
	public void meetingCreated(IMeeting meeting){
		activateContext(meetingContext(), meeting);
	}

	public void windowInitializedEventDone(WindowEvent event) {
		log.debug(">>> End window intialize event: " + event.getWindow().getUid());
		destroyContext(requestContext());
		deactivateContext(sessionContext(), event.getWindow().getDefaultChannel().getUserSession());
		deactivateContext(channelContext(), event.getWindow().getDefaultChannel());
		deactivateContext(connectionContext(), event.getWindow().getConnection());
		deactivateContext(windowContext(),  event.getWindow());
		
		if(meetingContext().isActive()){
			deactivateContext(meetingContext(), event.getWindow().getMeeting());
		}
		
	}	

	
	public void windowDestroyed(WindowEvent event) {
		log.debug(">>> Begin window destroy event: " + event.getWindow().getUid());
		activateContext(requestContext(), event);
		activateContext(sessionContext(), event.getWindow().getDefaultChannel().getUserSession());
		activateContext(channelContext(), event.getWindow().getDefaultChannel());
		activateContext(connectionContext(), event.getWindow().getConnection());
		activateContext(windowContext(), event.getWindow());
		if(event.getWindow().getMeeting() != null){
			activateContext(meetingContext(), event.getWindow().getMeeting());
		}
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}
	
	public void windowDestroyedEventDone(WindowEvent event) {
		log.debug(">>> End window destroy event: " + event.getWindow().getUid());
		destroyContext(requestContext());
		deactivateContext(sessionContext(), event.getWindow().getDefaultChannel().getUserSession());
		deactivateContext(channelContext(), event.getWindow().getDefaultChannel());
		deactivateContext(connectionContext(), event.getWindow().getConnection());
		deactivateContext(windowContext(),  event.getWindow());
		
		if(meetingContext().isActive()){
			deactivateContext(meetingContext(), event.getWindow().getMeeting());
		}
	}	

	public void connectionInitialized(ConnectionEvent event) {
		log.debug(">>> Begin connection intialize event: " + event.getConnection().getServiceId());
		activateContext(requestContext(), event);
		activateContext(connectionContext(), event.getConnection());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}
	
	public void connectionInitializedEventDone(ConnectionEvent event) {
		log.debug(">>> End connection intialize event: " + event.getConnection().getServiceId());
		destroyContext(requestContext());
		deactivateContext(connectionContext(), event.getConnection());
	}

	public void connectionDestroyed(ConnectionEvent event) {
		log.debug(">>> Begin connection destroyed event: " + event.getConnection().getServiceId());
		activateContext(requestContext(), event);
		activateContext(connectionContext(), event.getConnection());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(event);
	}	
	
	public void connectionDestoryedEventDone(ConnectionEvent event) {
		log.debug(">>> End connection destroyed event: " + event.getConnection().getServiceId());
		destroyContext(requestContext());
		destroyContext(connectionContext());
	}
	
	public void buddyEventInitialized(BuddyEvent buddyEvent) {
		log.debug(">>> Begin buddy event: " + buddyEvent.getBuddy().getUid());
		activateContext(requestContext(), buddyEvent);
		activateContext(connectionContext(), buddyEvent.getConnection());
		CDIRequestObjectInThreadHolder.getInstance().setRequestObjectInThread(buddyEvent);
	}
	
	public void buddyEventDestroyed(BuddyEvent buddyEvent) {
		log.debug(">>> End buddy event: " + buddyEvent.getBuddy().getUid());
		destroyContext(requestContext());
		deactivateContext(connectionContext(), buddyEvent.getConnection());
	}
	
	
	private void destroyContext(AbstractBoundContext<IAttributes> context) {
		context.invalidate();
		context.deactivate();
	}

//	protected BeanStore getBeanStore(IAttributes attributes) {
//		return AttributesBackedBeanStore.of(attributes, attributes.getClass().getName());
//	}

	

	private void activateContext(AbstractBoundContext<IAttributes> context, IAttributes storage) {
		if (storage == null) {
			throw new IllegalArgumentException("null storage for " + context);
		}
		context.associate(storage);
		context.activate();
	}
	
	private void deactivateContext(AbstractBoundContext<IAttributes> context, IAttributes storage) {
		context.deactivate();
		context.dissociate(storage);
	}
	

//	private void activateContext(AbstractApplicationContext context, BeanStore beanStore) {
//		if (beanStore == null) {
//			throw new IllegalArgumentException("null bean store for " + context);
//		}
//		context.setBeanStore(beanStore);
//		context.setActive(true);
//	}
//
//	private void destroyContext(AbstractApplicationContext context) {
//		if (context.getBeanStore() == null) {
//			return;
//		}
//		activateContext(context, context.getBeanStore());
//		context.destroy();
//		deactivateContext(context);
//	}
//
//	private void deactivateContext(AbstractApplicationContext context) {
//		context.setBeanStore(null);
//		context.setActive(false);
//	}
//
//	private void deactivateContext(AbstractThreadLocalMapContext context) {
//		context.setBeanStore(null);
//		context.setActive(false);
//	}
//
//	
//	private void deactivateDependentContext() {
//		dependentContext.setActive(false);
//	}

}
