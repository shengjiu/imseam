package com.imseam.cdi.chatlet.components;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.imseam.cdi.chatlet.event.ConnectionRequest;
import com.imseam.cdi.chatlet.event.MeetingEvent;
import com.imseam.cdi.chatlet.ext.annotation.RequestObject;
import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IBuddy;
import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.ISession;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.SessionEvent;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.cluster.IClusterCache;


public class CDIContextObjectProducer {

    @Produces
    @ApplicationScoped
	public IApplication getApplication() {
		return CDIRequestObjectInThreadHolder.getInstance().getApplication();
	}

    @SuppressWarnings("unchecked")
	private <T> T getRequestObject(Class<T> type){
    	Object requestObject = CDIRequestObjectInThreadHolder.getInstance().getRequestObjectInThread();
    	if(requestObject == null || !type.isInstance(requestObject)) return null;
    	return (T)requestObject;
    }
    
    
    @Produces
    @RequestObject
	public IAttributes getRequestObject(){
    	return getRequestObject(IAttributes.class);
	}
    
    @Produces
	public IClusterCache getClusterCache(){
    	return CDIRequestObjectInThreadHolder.getInstance().getApplication().getClusterCache();
	}
    
    @Produces
	public SessionEvent getSessionEvent(){
    	return getRequestObject(SessionEvent.class);
	}
	
    @Produces
	public UserJoinWindowEvent getUserJoinWindowEvent(){
    	return getRequestObject(UserJoinWindowEvent.class);
	}
	
    @Produces
	public WindowEvent getWindowEvent(){
    	return getRequestObject(WindowEvent.class);
	}
	
//    @Produces
//    @IMRequestScoped
//	public IMessageSender getMessageSender(){
//		return CDIRequestObjectInThreadHolder.getInstance().getMessageSender();
//	}
	
    @Produces
	public MeetingEvent getFromMeetingRequest(){
    	return getRequestObject(MeetingEvent.class);
	}
	
    @Produces
	public ConnectionRequest getConnectionRequest(){
    	return getRequestObject(ConnectionRequest.class);
	}
	
    @Produces
	public BuddyEvent getBuddyEvent(){
    	return getRequestObject(BuddyEvent.class);
	}

    @Produces
	public IUserRequest getUserRequest(){
    	return getRequestObject(IUserRequest.class);
	}

    @Produces
	public ConnectionEvent getConnectionEvent(){
    	return getRequestObject(ConnectionEvent.class);
	}

    private ContextProducer getContextProducer(){
    	SessionEvent sessionEvent = getSessionEvent(); 
    	if( sessionEvent != null){
    		return new SessionEventContextProducer(sessionEvent);
    	}
    	UserJoinWindowEvent userJoinWindowEvent = getUserJoinWindowEvent();
    	if(userJoinWindowEvent != null){
    		return new UserJoinWindowEventContextProducer(userJoinWindowEvent);
    	}
    	WindowEvent windowEvent = getWindowEvent();
    	if(windowEvent != null){
    		return new WindowEventContextProducer(windowEvent);
    	}
    	MeetingEvent fromMeetingRequest = getFromMeetingRequest();
    	if(fromMeetingRequest != null){
    		return new FromMeetingRequestContextProducer(fromMeetingRequest);
    	}
    	ConnectionRequest connectionRequest = getConnectionRequest();
    	if(connectionRequest != null){
    		return new ConnectionRequestContextProducer(connectionRequest);
    	}
    	BuddyEvent buddyEvent = getBuddyEvent();
    	if(buddyEvent != null){
    		return new BuddyEventContextProducer(buddyEvent);
    	}
    	IUserRequest userRequest = getUserRequest();
    	if(userRequest != null){
    		return new UserRequestContextProducer(userRequest);
    	}
    	ConnectionEvent connectionEvent = getConnectionEvent();
    	if(connectionEvent != null){
    		return new ConnectionEventContextProducer(connectionEvent);
    	}
    	return null;
    }
    

    @Produces
    public IConnection getConnection(){
    	ContextProducer contextProducer = getContextProducer();
    	if(contextProducer == null) return null;
    	return	contextProducer.getConnection();
	}
	
    @Produces
    public ISession getSession(){
    	ContextProducer contextProducer = getContextProducer();
    	if(contextProducer == null) return null;
    	return	contextProducer.getSession();
	}
    @Produces
    public IWindow getWindow(){
    	ContextProducer contextProducer = getContextProducer();
    	if(contextProducer == null) return null;
    	return	contextProducer.getWindow();
	}
    
    @Produces
    public IMessageSender getSender(){
    	IWindow window = getWindow();
    	if(window == null) return null;
    	return	window.getMessageSender();
	}
    
    @Produces
    public IBuddy getBuddy(){
    	ContextProducer contextProducer = getContextProducer();
    	if(contextProducer == null) return null;
    	return	contextProducer.getBuddy();
	}
    @Produces
    public IChannel getChannel(){
    	ContextProducer contextProducer = getContextProducer();
    	if(contextProducer == null) return null;
    	return	contextProducer.getChannel();
	}
    @Produces
    public IMeeting getMeeting(){
    	ContextProducer contextProducer = getContextProducer();
    	if(contextProducer == null) return null;
    	return	contextProducer.getMeeting();
	}   
    
    class ContextProducer{
    	IConnection getConnection(){
    		return null;
    	}
    	
    	ISession getSession(){
    		return null;
    	}
    	
    	IWindow getWindow(){
    		return null;
    	}
    	
    	IBuddy getBuddy(){
    		return null;
    	}
    	
    	IChannel getChannel(){
    		return null;
    	}
    	
    	IMeeting getMeeting(){
    		return null;
    	}
    }
    
    class SessionEventContextProducer extends ContextProducer{
    	private SessionEvent sessionEvent;
    	
    	SessionEventContextProducer(SessionEvent sessionEvent){
    		this.sessionEvent = sessionEvent;
    	}
		@Override
		public ISession getSession() {
			return sessionEvent.getSession();
		}

		@Override
		public IBuddy getBuddy() {
			return sessionEvent.getSession().getBuddy();
		}
    	
    }
    class UserJoinWindowEventContextProducer extends ContextProducer{
    	
    	private UserJoinWindowEvent userJoinWindowEvent;
    	
    	UserJoinWindowEventContextProducer(UserJoinWindowEvent userJoinWindowEvent){
    		this.userJoinWindowEvent = userJoinWindowEvent;
    	}
    	
		@Override
		public IConnection getConnection() {
			return userJoinWindowEvent.getChannel().getWindow().getConnection();
		}

		@Override
		public ISession getSession() {
			return userJoinWindowEvent.getChannel().getUserSession();
		}

		@Override
		public IWindow getWindow() {
			return userJoinWindowEvent.getChannel().getWindow();
		}

		@Override
		public IBuddy getBuddy() {
			return userJoinWindowEvent.getChannel().getBuddy();
		}

		@Override
		public IChannel getChannel() {
			return userJoinWindowEvent.getChannel();
		}

		@Override
		public IMeeting getMeeting() {
			return userJoinWindowEvent.getChannel().getWindow().getMeeting();
		}
    	
    }
    class WindowEventContextProducer extends ContextProducer{
    	private WindowEvent windowEvent;
    	WindowEventContextProducer(WindowEvent windowEvent){
    		this.windowEvent = windowEvent;
    	}
    	
		@Override
		public IConnection getConnection() {
			return windowEvent.getConnection();
		}

		@Override
		public ISession getSession() {
			return windowEvent.getWindow().getDefaultChannel().getUserSession();
		}

		@Override
		public IWindow getWindow() {
			return windowEvent.getWindow();
		}

		@Override
		public IBuddy getBuddy() {
			return windowEvent.getWindow().getDefaultChannel().getBuddy();
		}

		@Override
		public IChannel getChannel() {
			return windowEvent.getWindow().getDefaultChannel();
		}

		@Override
		public IMeeting getMeeting() {
			return windowEvent.getWindow().getMeeting();
		}
    	
    }

    class FromMeetingRequestContextProducer extends ContextProducer{

    	private MeetingEvent fromMeetingRequest;
    	FromMeetingRequestContextProducer(MeetingEvent fromMeetingRequest){
    		this.fromMeetingRequest = fromMeetingRequest;
    	}
		@Override
		public IConnection getConnection() {
			return fromMeetingRequest.getWindow().getConnection();
		}

		@Override
		public ISession getSession() {
			return fromMeetingRequest.getWindow().getDefaultChannel().getUserSession();
		}

		@Override
		public IWindow getWindow() {
			return fromMeetingRequest.getWindow();
		}

		@Override
		public IBuddy getBuddy() {
			return fromMeetingRequest.getWindow().getDefaultChannel().getBuddy();
		}

		@Override
		public IChannel getChannel() {
			return fromMeetingRequest.getWindow().getDefaultChannel();
		}

		@Override
		public IMeeting getMeeting() {
			return fromMeetingRequest.getWindow().getMeeting();
		}
    	
    }
    class ConnectionRequestContextProducer extends ContextProducer{

    	private ConnectionRequest connectionRequest;
    	
    	ConnectionRequestContextProducer(ConnectionRequest connectionRequest){
    		this.connectionRequest = connectionRequest;
    	}
    	
		@Override
		public IConnection getConnection() {
			return connectionRequest.getConnection();
		}

    }
    class BuddyEventContextProducer extends ContextProducer{

    	private BuddyEvent buddyEvent;
    	BuddyEventContextProducer(BuddyEvent buddyEvent){
    		this.buddyEvent = buddyEvent;
    	}
    	
		@Override
		public IConnection getConnection() {
			return buddyEvent.getConnection();
		}

		@Override
		public IBuddy getBuddy() {
			return buddyEvent.getBuddy();
		}
    }
    class UserRequestContextProducer extends ContextProducer{

    	private IUserRequest userRequest;
    	
    	UserRequestContextProducer(IUserRequest userRequest){
    		this.userRequest = userRequest;
    	}
    	
		@Override
		public IConnection getConnection() {
			return userRequest.getRequestFromChannel().getWindow().getConnection();
		}

		@Override
		public ISession getSession() {
			return userRequest.getRequestFromChannel().getUserSession();
		}

		@Override
		public IWindow getWindow() {
			return userRequest.getRequestFromChannel().getWindow();
		}

		@Override
		public IBuddy getBuddy() {
			return userRequest.getRequestFromChannel().getBuddy();
		}

		@Override
		public IChannel getChannel() {
			return userRequest.getRequestFromChannel();
		}

		@Override
		public IMeeting getMeeting() {
			return userRequest.getRequestFromChannel().getWindow().getMeeting();
		}
    	
    }
    class ConnectionEventContextProducer extends ContextProducer{
    	
    	private ConnectionEvent connectionEvent;
    	
    	ConnectionEventContextProducer(ConnectionEvent connectionEvent){
    		this.connectionEvent = connectionEvent;
    	}
    	
		@Override
		public IConnection getConnection() {
			return connectionEvent.getConnection();
		}
    }
    
}
