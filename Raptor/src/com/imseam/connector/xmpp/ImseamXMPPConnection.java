package com.imseam.connector.xmpp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

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
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.core.AbstractConnection;

public class ImseamXMPPConnection extends AbstractConnection implements RosterListener, MessageListener, ChatManagerListener{

	private static Log log = LogFactory.getLog(ImseamXMPPConnection.class);

	private XMPPConnection  xmppConnection;
	
	private static String HOST_IP = "host-ip";
	
	private static String HOST_PORT = "host-port";
	
	private static String HOST_DOMAIN = "host-domain";
	
	private final static Executor activeConversationBackgroundExecutor = Executors.newSingleThreadExecutor();


	private Map<String, ImseamXMPPConversation> conversationMap = new HashMap<String, ImseamXMPPConversation>();

	public ImseamXMPPConnection(IChatletApplication application,
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

		ConnectionConfiguration connectionConfig = new ConnectionConfiguration(hostIP, Integer.valueOf(hostPort), hostDomain);
		xmppConnection = new XMPPConnection(connectionConfig);

	}

	
	public boolean connect() {
		try {
			xmppConnection.connect();
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			xmppConnection.login(this.getHostUserId(), this.getPassword());
			xmppConnection.getRoster().addRosterListener(this);
			xmppConnection.getChatManager().addChatListener(this);
			Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
			Collection<RosterEntry> entries = xmppConnection.getRoster().getEntries();
			for(RosterEntry entry: entries){
				this.addMessengerUser(entry.getUser(), false);
				presenceChanged(xmppConnection.getRoster().getPresence(entry.getUser()));
			}
			connectionStarted();
	
			
		} catch (Exception e) {
			ExceptionUtil
					.wrapRuntimeException(String.format("Cannot connect to Google using Smack SDK(user: %s, Password: %s",
											this.getHostUserId(), this.getPassword()), e);
		}
		return true;
	}
	
	public void processMessage(Chat chat, Message xmppMsg){
		String body = xmppMsg.getBody();
		if(StringUtil.isNullOrEmptyAfterTrim(body)){
			log.info("XMPP received a null message!");
			return;
		}
		log.debug("EVENT: messageReceived: " +body);
		MessengerTextMessage message = new MessengerTextMessage(body);
		this.getConversation(chat).requestReceived(message, getUserNameWithoutResource(chat.getParticipant()));
		
	}
	
	public void chatCreated(Chat chat, boolean createdLocally){
		chat.addMessageListener(ImseamXMPPConnection.this);
		this.getConversation(chat);
	}

	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void disconnecting() {
		xmppConnection.disconnect();
		conversationMap.clear();
	}

	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		
		
		Presence presence = xmppConnection.getRoster().getPresence(messengerID);
		if (presence.getType() == Presence.Type.available) {
			return BuddyStatusEnum.online_available;
		}
		
		if (presence.getType() == Presence.Type.unavailable) {
			return BuddyStatusEnum.offline;
		}

		return BuddyStatusEnum.notexisting;
	}

	public XMPPConnection getXMPPConnection() {
		return this.xmppConnection;
	}

	private ImseamXMPPConversation getConversation(Chat xmppChat) {
		assert (xmppChat != null);
		assert(!StringUtil.isNullOrEmptyAfterTrim(xmppChat.getParticipant()));
		String userName = getUserNameWithoutResource(xmppChat.getParticipant());
		ImseamXMPPConversation conversation = this.conversationMap.get(userName);
		if (conversation == null) {
			conversation = new ImseamXMPPConversation(xmppChat, this);
			this.conversationMap.put(userName, conversation);
			fireWindowStarted(conversation);
		}
		conversation.setXMPPChat(xmppChat);
		
		return conversation;
	}

	public void internalWindowStopped(IMessengerWindow conversation) {
		ImseamXMPPConversation xmppConversation = (ImseamXMPPConversation)conversation;
		this.conversationMap.remove(getUserNameWithoutResource(xmppConversation.getID()));
		
	}
	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
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



	static String getUserNameWithoutResource(String userNameWithResource){
		int atIndex = userNameWithResource.indexOf("/");
        if (atIndex > 0) {
        	userNameWithResource = userNameWithResource.substring(0, atIndex);
        }
        return userNameWithResource;
	}
	
	class ActiveConversationCreatingRunnable implements Runnable{
		
		private XMPPConnection connection = null;
		private String userName = null;
		private MessageListener listener = null;

		ActiveConversationCreatingRunnable(XMPPConnection connection, String userName, MessageListener listener){
			assert(connection != null);
			assert(!StringUtil.isNullOrEmptyAfterTrim(userName));
			assert(listener != null);
			this.connection = connection;
			this.userName = userName;
			this.listener = listener;
		}
		
		public void run() {
			try{
				connection.getChatManager().createChat(userName, listener);
			}catch(Exception exp){
				log.error("Error happened int creating active chat thread", exp);
			}
		}
		
	}

	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		
		
	}

	@Override
	public boolean isInviteBuddySupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException {
		try{
			String userName = UidHelper.parseUseridFromBuddyUid(buddyUid);
			if(this.getBuddyStatus(buddyUid) == BuddyStatusEnum.online_available){
				activeConversationBackgroundExecutor.execute(new ActiveConversationCreatingRunnable(xmppConnection, userName, this));
			}else{
				callback.startWindowFailed(new StartActiveWindowException(String.format("Active conversation can only be started when the messenger user's (%s) current status is online_available!", buddyUid), buddyUid));
			}	
		}catch(Exception ioExp){
			callback.startWindowFailed(new StartActiveWindowException(String.format("Call active conversation for user (%s) IO error", buddyUid), ioExp, buddyUid));
		}			
	}
}
