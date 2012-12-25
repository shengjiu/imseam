package com.imseam.connector.xmpp.multiline;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.MessageEvent;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.chatlet.MessengerTextMessage;
import com.imseam.raptor.chatlet.MessengerBuddy;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.chatlet.UserSession;
import com.imseam.raptor.core.AbstractConnection;
import com.imseam.raptor.standard.UserSessionManager;

public class MultiLineXMPPConnection extends AbstractConnection implements RosterListener{
	private static Log log = LogFactory.getLog(MultiLineXMPPConnection.class);

	private static String HOST_IP = "host-ip";
	
	private static String HOST_PORT = "host-port";
	
	private static String HOST_DOMAIN = "host-domain";
	
	private static String LINE_NUMBER = "line-number";
	
	private int lineNumber;
	
	private final static Executor activeConversationBackgroundExecutor = Executors.newSingleThreadExecutor();
	
	private ConcurrentHashMap<String, UserHelper> userMap = new ConcurrentHashMap<String, UserHelper>();
	
	private XMPPConnection [] connectionArray = null;
	
	private XMPPConnectionListener [] listenerArray = null;
	
	public MultiLineXMPPConnection(IChatletApplication application,
			ConnectionConfig config) {
		super(application, config);
	}

	public void initialize() {

		if (StringUtil.isNullOrEmptyAfterTrim(this.getHostUserId())) {
			ExceptionUtil
					.createRuntimeException("\'email\' is not defined. The XMPP Connection requires \'email\' to be defined in the init-params.");
		}

		if (StringUtil.isNullOrEmptyAfterTrim( this.getPassword())) {
			ExceptionUtil
					.createRuntimeException("\'password\' is not defined. The XMPP Connection requires \'password\' to be defined in the init-params.");
		}
		
		String hostIP = config.get(HOST_IP);
		if (StringUtil.isNullOrEmptyAfterTrim( hostIP)) {
			ExceptionUtil
					.createRuntimeException("\'Host IP\' is not defined. The XMPP Connection requires \'host-ip\' to be defined in the init-params.");
		}
		
		String hostPort = config.get(HOST_PORT);
		if (StringUtil.isNullOrEmptyAfterTrim( hostPort)) {
			ExceptionUtil
					.createRuntimeException("\'Host Port\' is not defined. The XMPP Connection requires \'host-port\' to be defined in the init-params.");
		}
		
		String hostDomain = config.get(HOST_DOMAIN);
		if (StringUtil.isNullOrEmptyAfterTrim( hostDomain)) {
			ExceptionUtil
					.createRuntimeException("\'Host Domain\' is not defined. The XMPP Connection requires \'host-domain\' to be defined in the init-params.");
		}
		
		try{
			lineNumber = Integer.parseInt(config.get(LINE_NUMBER));
		}catch(Exception exp){
			ExceptionUtil
			.createRuntimeException("\'Line number\' is not defined. The Multiline XMPP Connection requires \'line-number\' to be defined in the init-params.");
		}
		if (lineNumber <= 0) {
			ExceptionUtil
					.createRuntimeException("\'Line number\' must be greater than Zero. The Multiline XMPP Connection requires \'line-number\' to be defined in the init-params.");
		}

		ConnectionConfiguration connectionConfig = new ConnectionConfiguration(hostIP, Integer.valueOf(hostPort), hostDomain);
		
		
		connectionArray = new XMPPConnection[lineNumber];
		this.listenerArray = new XMPPConnectionListener[lineNumber];
		for(int i =0 ; i < connectionArray.length; i++){
			connectionArray[i] = new XMPPConnection(connectionConfig);
			listenerArray[i] = new XMPPConnectionListener(connectionArray[i]);
		}
	}

	
	public boolean connect() {
		try {
			for(int i = 0; i < connectionArray.length; i++){
				connectionArray[i].connect();
				connectionArray[i].login(this.getHostUserId() + i, this.getPassword());
				connectionArray[i].getChatManager().addChatListener(listenerArray[i]);
			}

			//The first connection will be treated as the main line to monitor roster events
			connectionArray[0].getRoster().addRosterListener(this);
			Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
			Collection<RosterEntry> entries = connectionArray[0].getRoster().getEntries();
			for(RosterEntry entry: entries){
				this.addMessengerUser(entry.getUser(), false);
				presenceChanged(connectionArray[0].getRoster().getPresence(entry.getUser()));
			}
			
		} catch (Exception e) {
			ExceptionUtil
					.wrapRuntimeException(String.format("Cannot connect to XMPP Server using Smack SDK(user: %s, Password: %s",
											this.getHostUserId(), this.getPassword()), e);
		}
		return true;
	}
	

	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void disconnecting() {
		for(int i = 0; i < connectionArray.length; i++){
			connectionArray[i].disconnect();
		}
		userMap.clear();
	}

	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		
		Presence presence = connectionArray[0].getRoster().getPresence(messengerID);
		if (presence.getType() == Presence.Type.available) {
			return BuddyStatusEnum.online_available;
		}
		
		if (presence.getType() == Presence.Type.unavailable) {
			return BuddyStatusEnum.offline;
		}

		return BuddyStatusEnum.notexisting;
	}

	private MultiLineXMPPConversation getConversation(XMPPConnection xmppConnection, Chat xmppChat, boolean create) {
		assert (xmppChat != null);
		assert(!StringUtil.isNullOrEmptyAfterTrim(xmppChat.getParticipant()));
		
		String userName = getUserNameWithoutResource(xmppChat.getParticipant());
		if(!create && this.userMap.get(userName) == null){
			return null;
		}

		UserHelper user = this.createUserIfAbsent(userName);
		return user.getConversation(xmppConnection, xmppChat, create);
	}

	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	@Override
	public int getWindowLimit(){
		return this.lineNumber;
	}
	
	public void entriesAdded(Collection<String> entries) {
		
		log.debug("EVENT: entriesAdded:"); 
		for(String entry : entries){
			log.debug("Entry: (" + entry + ")\r\n");
			this.addMessengerUser(getUserNameWithoutResource(entry), true);
			
		}
		
		
	}

	public void entriesUpdated(Collection<String> entries) {
		log.debug("EVENT: entriesUpdated: not implemented");
		for(String entry : entries){
			log.debug("Entry: " + entry + "r/n");
		}
		
	}

	public void entriesDeleted(Collection<String> entries) {
		
		log.debug("EVENT: entriesDeleted:"); 
		for(String entry : entries){
			log.debug("Entry: (" + entry + ")\r\n");
			this.removeMessengerUser(getUserNameWithoutResource(entry), true);
		}
	}

	public void presenceChanged(Presence presence) {
		this.fireBuddyStatusChange(getUserNameWithoutResource(presence.getFrom()));
		log.debug("**EVENT: presenceChanged (" + presence.getFrom() + ")");
	}

	private UserHelper createUserIfAbsent(String userName){
		UserHelper user = this.userMap.get(userName);
		if (user == null) {
			user = new UserHelper(userName);
			UserHelper oldUser = this.userMap.putIfAbsent(userName, user);
			if(oldUser != null){
				user = oldUser;
			}
		}
		return user;
	}
	
	public void internalWindowStopped(IMessengerWindow conversation) {
		assert(conversation != null);
		MultiLineXMPPConversation xmppConversation = (MultiLineXMPPConversation)conversation;
		
		String userName = xmppConversation.getParticipantID();
		
		UserHelper user = this.userMap.get(userName);
		
		assert(user != null);
		
		user.conversationStopped(conversation);
	}
	
	@Override
	public void sessionStopped(UserSession oldSession){
		assert(oldSession != null);
		this.userMap.remove(oldSession.getMessengerUser().getUid());
	}

	static String getUserNameWithoutResource(String userNameWithResource){
		int atIndex = userNameWithResource.indexOf("/");
        if (atIndex > 0) {
        	userNameWithResource = userNameWithResource.substring(0, atIndex);
        }
        return userNameWithResource;
	}
	
	class ActiveConversationCreatingRunnable implements Runnable{
		
		private XMPPConnection connection = null;
		private String userID = null;
		private MessageListener listener = null;

		ActiveConversationCreatingRunnable(XMPPConnection connection, String userID, MessageListener listener){
			assert(connection != null);
			assert(!StringUtil.isNullOrEmptyAfterTrim(userID));
			assert(listener != null);
			this.connection = connection;
			this.userID = userID;
			this.listener = listener;
		}
		
		public void run() {
			try{
				//why create the session: because the sessionManager can control the life time for timeout
				UserSessionManager sessionManager = (UserSessionManager) getApplication().getSessionManager();
				MessengerBuddy user = getBuddy(userID, true);
				sessionManager.createUserSession(user);
				connection.getChatManager().createChat(userID, listener);
			}catch(Exception exp){
				log.error("Error happened int creating active chat thread", exp);
			}
		}
		
	}
	
	class XMPPConnectionListener implements MessageListener, ChatManagerListener{

		private XMPPConnection xmppConnection = null;
		
		XMPPConnectionListener(XMPPConnection xmppConnection){
			this.xmppConnection = xmppConnection;
		}
		public void processMessage(Chat chat, Message xmppMsg) {
			String body = xmppMsg.getBody();
			
			if(StringUtil.isNullOrEmptyAfterTrim(body)){
				log.debug("XMPP received a null message!");
				Iterator<PacketExtension> iterator = xmppMsg.getExtensions().iterator();
				while(iterator.hasNext()){
					PacketExtension extension = iterator.next();
					if(extension instanceof MessageEvent){
						MessageEvent event = (MessageEvent) extension;
						if(event.isCancelled()){
							MultiLineXMPPConversation conversation = getConversation(xmppConnection, chat, false);
							if(conversation != null){
								fireWindowStopped(conversation);
								return;
							}
						}
					}
				}
			
				return;
			}
			log.debug("EVENT: messageReceived: " +body);
			MessengerTextMessage message = new MessengerTextMessage(body);
			MultiLineXMPPConversation conversation = getConversation(xmppConnection, chat, true);
			assert(conversation != null);
			conversation.requestReceived(message, getUserNameWithoutResource(chat.getParticipant()));
		}

		public void chatCreated(Chat chat, boolean createdLocally) {
			chat.addMessageListener(this);
			if(createdLocally){
				getConversation(xmppConnection, chat, true);
				
				String userName = getUserNameWithoutResource(chat.getParticipant());
				UserHelper user = createUserIfAbsent(userName);
				user.removePendingConversationCreated(xmppConnection);
			}
		}
	}
	
	class UserHelper{
		private String userID;
		private ConcurrentHashMap<XMPPConnection, MultiLineXMPPConversation> connection2ConversationMap = new ConcurrentHashMap<XMPPConnection, MultiLineXMPPConversation>();
		private ConcurrentHashMap<XMPPConnection, Long> pendingConversationMap = new ConcurrentHashMap<XMPPConnection, Long>(); 
		private ReentrantLock userLock = new ReentrantLock();
		
		UserHelper(String userID){
			this.userID = userID;
			assert(getBuddy(userID, false) != null);
		}
		
		//Assume one conversation only contains one user channel
		MultiLineXMPPConversation getConversation(XMPPConnection xmppConnection, Chat xmppChat, boolean create) {
			assert (xmppChat != null);
			assert(!StringUtil.isNullOrEmptyAfterTrim(xmppChat.getParticipant()));

			MultiLineXMPPConversation conversation = connection2ConversationMap.get(xmppConnection);
			if(conversation == null){
				if(!create) return null;
				
				conversation = new MultiLineXMPPConversation(userID, xmppChat, MultiLineXMPPConnection.this, xmppConnection);
				MultiLineXMPPConversation oldConversation = connection2ConversationMap.putIfAbsent(xmppConnection, conversation);
					
				if(oldConversation == null){
					removePendingConversationCreated(xmppConnection);
					fireWindowStarted(conversation);
				}
			}else{
				if(!conversation.access(userID)){
					if(!create) return null;
					conversation = new MultiLineXMPPConversation(userID, xmppChat, MultiLineXMPPConnection.this, xmppConnection);
					connection2ConversationMap.put(xmppConnection, conversation);
					removePendingConversationCreated(xmppConnection);
					fireWindowStarted(conversation);
				}
			}
			conversation.setXMPPChat(xmppChat);
			
			return conversation;
		}
		
		void conversationStopped(IMessengerWindow conversation) {
			assert(conversation != null);
			MultiLineXMPPConversation xmppConversation = (MultiLineXMPPConversation)conversation;
			
			boolean result = connection2ConversationMap.remove(xmppConversation.getXMPPConnection(), conversation);
			assert(result);
		}
		
		void startActiveConversation() throws StartActiveWindowException {
			
			userLock.lock();
			try{
				for(int i = 0; i < connectionArray.length; i++){
					if((connection2ConversationMap.get(connectionArray[i]) != null)){
						continue;
					}
					
					Long lastTryTime = pendingConversationMap.get(connectionArray[i]);
					long waitingTime = 60000;
					if((lastTryTime != null) && 
							(lastTryTime.longValue() + waitingTime > System.currentTimeMillis())) {
						continue;
					}
					pendingConversationMap.put(connectionArray[i], new Long(System.currentTimeMillis()));
					activeConversationBackgroundExecutor.execute(new ActiveConversationCreatingRunnable(connectionArray[i], userID, listenerArray[i]));
					return;
				}
				throw new StartActiveWindowException(String.format("Active conversation can only be started when the messenger user's (%s) current status is online_available!", userID), userID);
			}finally{
				userLock.unlock();
			}
				
			
		}
		
		void removePendingConversationCreated(XMPPConnection xmppConnection) {
			userLock.lock();
			try{
				pendingConversationMap.remove(xmppConnection);
			}finally{
				userLock.unlock();
			}
		}
		

	}

	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInviteBuddySupported() {
		return false;
	}

	@Override
	public void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException {
		try{
			String userName = UidHelper.parseUseridFromBuddyUid(buddyUid);
			if(this.getBuddyStatus(userName) == BuddyStatusEnum.online_available){
				UserHelper user = this.createUserIfAbsent(userName);
				
				user.startActiveConversation();
			}else{
				callback.startWindowFailed(new StartActiveWindowException(String.format("Active conversation can only be started when the messenger user's (%s) current status is online_available!", buddyUid), buddyUid));
			}
		}catch(Exception exp){
			if(exp instanceof StartActiveWindowException){
				callback.startWindowFailed(exp);
			}else{
				callback.startWindowFailed(new StartActiveWindowException(String.format("Call active conversation for user (%s) IO error", buddyUid), exp, buddyUid));
			}
		}		
	}
}
