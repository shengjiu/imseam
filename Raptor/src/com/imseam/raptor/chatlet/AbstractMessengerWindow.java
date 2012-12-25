package com.imseam.raptor.chatlet;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.exception.ChannelExpiredException;
import com.imseam.chatlet.exception.SessionExpiredException;
import com.imseam.chatlet.listener.event.UserJoinWindowEvent;
import com.imseam.raptor.IEventListenerManager;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.IRequestProcessor;
import com.imseam.raptor.standard.MessageSender;
import com.imseam.raptor.standard.UserSessionManager;

public abstract class AbstractMessengerWindow implements IMessengerWindow {

	private static Log log = LogFactory.getLog(AbstractMessengerWindow.class);

	private IMessengerConnection connection = null;

	private IRequestProcessor requestProcessor = null;

	private IEventListenerManager eventListenerManager = null;

	private WindowContext windowContext = null;

	private UserSessionManager sessionManager = null;
	
	private MessageSender messageSender = new MessageSender(this);

	protected AbstractMessengerWindow(IMessengerConnection connection,
			String userId) {
		this(connection, 0, userId);
	}

	protected AbstractMessengerWindow(IMessengerConnection connection, int defaultChannelIndex,
			String... userIds) {

		this.connection = connection;
		this.eventListenerManager = connection.getApplication()
				.getEventListenerManager();
		this.requestProcessor = connection.getApplication()
				.getRequestProcessor();

		assert (connection != null);
		assert (eventListenerManager != null);
		assert (requestProcessor != null);
		assert ((userIds != null) && (userIds.length > 0));
		assert ((defaultChannelIndex < 0) || (defaultChannelIndex < userIds.length));
		sessionManager = (UserSessionManager) connection
				.getApplication().getSessionManager();

		windowContext = new WindowContext(this);

		ChannelContext[] newChannels = new ChannelContext[userIds.length];
		for (int i = 0; i < userIds.length; i++) {
			MessengerBuddy user = connection.getBuddyByUserId(userIds[i]);
			sessionManager.createUserSession(user);
			newChannels[i] = new ChannelContext(this, user);
			windowContext.addOnboardChannel(newChannels[i]);
		}
		
		if(defaultChannelIndex >= 0){
			windowContext.setDefaultChannel(newChannels[defaultChannelIndex].getBuddy().getUid());
		}
	}
	@Override
	public void closeWindow() {
		connection.internalWindowStopped(this);
	}

	@Override
	public IMessengerConnection getConnection() {
		return connection;
	}

	@Override
	public WindowContext getWindowContext() {
		return windowContext;
	}

	private ChannelContext getChannel(String userId) {
		return (ChannelContext) getWindowContext().getChannelByBuddyUid(userId);
	}

	@Override
	public ChannelContext[] getOnboardChannels() {
		Collection<ChannelContext> channelContexts = getWindowContext()
				.getOnboardChannels();
		return channelContexts.toArray(new ChannelContext[channelContexts
				.size()]);

	}

	@Override
	public void userJoin(String... userIds) {
		assert(userIds != null && userIds.length > 0);

		for (String userId : userIds) {
			MessengerBuddy buddy = connection.getBuddyByUserId(userId);
			if (buddy == null) {
				log.warn(String.format(
						"Trying to add an channel not in the friend list",
						userId));
				continue;
			}

			ChannelContext channel = getChannel(buddy.getUid());
			if (channel != null) {
				log.warn(String.format(
						"Channel for user %s is existing already!", userId));
				continue;
			}
			sessionManager.createUserSession(buddy);
			
			channel = new ChannelContext(this, buddy); 
			
			this.connection.getConnectionContext().addBuddyActiveWindow(buddy.getUid(), this.getWindowContext());
			eventListenerManager.fireEvent(EventTypeEnum.UserJoinWindow,
					createUserJoinConversationEvent(channel));

		}

	}

	@Override
	public void userLeave(String... userIds) {
		assert(userIds != null && userIds.length > 0);
		for (String userId : userIds) {
			MessengerBuddy buddy = connection.getBuddyByUserId(userId);
			if (buddy == null) {
				log.warn(String.format(
						"Trying to add an channel not in the friend list",
						userId));
				continue;
			}

			ChannelContext channel = getChannel(buddy.getUid());
			if (channel == null) {
				log.warn(String.format(
						"Channel for user %s is removed already!", userId));
				continue;
			}
			this.connection.getConnectionContext().removeBuddyActiveWindow(buddy.getUid(), this.getWindowContext());

			eventListenerManager.fireEvent(EventTypeEnum.UserLeaveWindow,
					createUserJoinConversationEvent(channel));
			
		}

	}

	private UserJoinWindowEvent createUserJoinConversationEvent(
			ChannelContext channel) {
		return new UserJoinWindowEvent(this, 
				channel);
	}

	public boolean access(String userId){
		try{
			MessengerBuddy user = connection.getBuddyByUserId(userId);
			if (user == null) {
				log.warn(String.format(
						"Trying to add an channel not in the friend list",
						userId));
				return false;
			}

			
			ChannelContext channel =this.getChannel(user.getUid());
			if(channel == null){
				log.warn("The channel cannot be found in the conversation for user: " + userId);
				return false;
			}
			channel.access();
		}catch (SessionExpiredException sexp) {
			log.warn("If session expired, the application should create a new session",
					sexp);
			return false;
		}catch (ChannelExpiredException cexp) {
			log.warn("If channel expired, the application should create a new channel",
					cexp);
			return false;
		}
		return true;
	}
	
	@Override
	public void requestReceived(IChatletMessage message, String userId) {
		try {
			MessengerBuddy user = connection.getBuddyByUserId(userId);
			if (user == null) {
				log.warn(String.format(
						"Trying to add an channel not in the friend list",
						userId));
				return;
			}

			
			ChannelContext channel =this.getChannel(user.getUid());
			
			channel.access();
			this.requestProcessor.requestReceived(message, user.getUid(), this);
		} catch (SessionExpiredException sexp) {
			log.error("If session expired, this method should not be called in the current logic",
							sexp);
		} catch (Exception exp) {
			log.error("Error happened during process message", exp);
		}

	}

	abstract public void sendResponse(IChatletMessage ... responses);
	
	@Override
	public IMessageSender getMessageSender() {
		return messageSender;
	}	
	
	public ChannelContext getDefaultChannel() {
		return this.windowContext.getDefaultChannel();
	}

	@Override
	public String toString() {
		String result = "(";
		for (IChannel channel : this.getOnboardChannels()) {
			result += channel + ", ";
		}
		result += ")";
		return result;
	}

}
