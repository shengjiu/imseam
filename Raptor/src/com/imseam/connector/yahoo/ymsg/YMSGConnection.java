package com.imseam.connector.yahoo.ymsg;



import ymsg.network.event.SessionChatEvent;
import ymsg.network.event.SessionConferenceEvent;
import ymsg.network.event.SessionErrorEvent;
import ymsg.network.event.SessionEvent;
import ymsg.network.event.SessionExceptionEvent;
import ymsg.network.event.SessionFileTransferEvent;
import ymsg.network.event.SessionFriendEvent;
import ymsg.network.event.SessionListener;
import ymsg.network.event.SessionNewMailEvent;
import ymsg.network.event.SessionNotifyEvent;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.core.AbstractConnection;

public class YMSGConnection extends AbstractConnection implements SessionListener {

	protected YMSGConnection(IChatletApplication application, ConnectionConfig config) {
		super(application, config);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reConnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void internalWindowStopped(IMessengerWindow window) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInviteBuddySupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buzzReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chatConnectionClosed(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chatLogoffReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chatLogonReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chatMessageReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chatUserUpdateReceived(SessionChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conferenceInviteDeclinedReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conferenceInviteReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conferenceLogoffReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conferenceLogonReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void conferenceMessageReceived(SessionConferenceEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contactRejectionReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contactRequestReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorPacketReceived(SessionErrorEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fileTransferReceived(SessionFileTransferEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void friendAddedReceived(SessionFriendEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void friendRemovedReceived(SessionFriendEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void friendsUpdateReceived(SessionFriendEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputExceptionThrown(SessionExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newMailReceived(SessionNewMailEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyReceived(SessionNotifyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void offlineMessageReceived(SessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void disconnecting() {
		// TODO Auto-generated method stub
		
	}

//	private static Log log = LogFactory.getLog(YMSGConnection.class);
//	Session yahooMessengerSession = null;
//	private final String friendGroupName = "Friends";
//
//	private Map<String, YMSGConversation> conversationMap = new HashMap<String, YMSGConversation>();
//
//	public YMSGConnection(IChatletApplication application,
//			ConnectionConfig config) {
//		super(application, config);
//	
//	}
//
//	public boolean connect() {
//		log.debug("call the method startingConnection");
//		
//		try {
//			yahooMessengerSession = new Session();
//			yahooMessengerSession.setStatus(StatusConstants.STATUS_AVAILABLE);
//			
//			yahooMessengerSession.addSessionListener(this);
//			
//			log.info("Attempting YMSG login:" + getHostUserUID());
//			yahooMessengerSession.login(getHostUserUID(), getPassword());
//			if(yahooMessengerSession.getSessionStatus()==StatusConstants.MESSAGING){
//				log.info("YMSG login succeed:" + getHostUserUID());
//			}else{
//				log.info("YMSG login failed:" + getHostUserUID());
//				return false;
//			}
//			
//
//			Hashtable users = yahooMessengerSession.getUsers();
//			for(Iterator i= users.keySet().iterator(); i.hasNext(); ){
//				this.addMessengerUser((String)i.next(), false);
//			}
//
//			
////			YahooGroup[] yg = yahooMessengerSession.getGroups();
////			for(int i=0;i<yg.length;i++)
////			{   System.out.println(yg[i].getName());
////			    for(int j=0;j<yg[i].getMembers().size();j++)
////			    {   YahooUser yu = (YahooUser)yg[i].getMembers().get(j);
////			        System.out.println("  "+yu.toString());
////			    }
////			}
//			
//			
//			
//		} catch (Exception e) {
//			ExceptionUtil
//					.wrapRuntimeException(String.format("Cannot connect to Yahoo using YMSG SDK(user: %s, Password: %s",
//											this.getHostUserUID(), this.getPassword()), e);
//		}
//		
//		return true;
//	}
//
//	@Override
//	protected void disconnecting() {
//		log.debug("call the method disconnecting");
//		try {
//			yahooMessengerSession.logout();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	private YMSGConversation getConversation(String userName) {
//		log.debug("call the method getConversation");
//		assert (!StringUtil.isNullOrEmptyAfterTrim(userName));
//		YMSGConversation conversation = this.conversationMap.get(userName);
//		if (conversation == null) {
//			conversation = new YMSGConversation(yahooMessengerSession, userName, this);
//			this.conversationMap.put(userName, conversation);
//			fireWindowStarted(conversation);
//		}
//		return conversation;
//	}
//
//
//	public void initialize() {
//		log.debug("call the method initialize");
//
//		if (StringUtil.isNullOrEmptyAfterTrim(this.getHostUserUID())) {
//			ExceptionUtil
//					.createRuntimeException("\'email\' is not defined. The JMSN Connection requires \'email\' to be defined in the init-params.");
//		}
//
//		if (StringUtil.isNullOrEmptyAfterTrim( this.getPassword())) {
//			ExceptionUtil
//					.createRuntimeException("\'password\' is not defined. The JMSN Connection requires \'password\' to be defined in the init-params.");
//		}
//		
//	}
//
//	public void fileTransferReceived(SessionFileTransferEvent arg0) {
//		log.debug("call the method fileTransferReceived");
//		// TODO Auto-generated method stub
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//		
//		
//	}
//
//	public void connectionClosed(SessionEvent arg0) {
//		log.debug("call the method connectionClosed");
//		
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//		
//		
//	}
//
//	public void listReceived(SessionEvent sessionEvent) {
//		log.debug("call the method listReceived");
//		
//		Hashtable users = yahooMessengerSession.getUsers();
//		for(Iterator i= users.keySet().iterator(); i.hasNext(); ){
//			this.addMessengerUser((String)i.next(), false);
//		}
//		
//	}
//
//	public void messageReceived(SessionEvent sessionEvent) {
//		log.debug("call the method messageReceived");
//		MessengerTextMessage message = new MessengerTextMessage(sessionEvent.getMessage());
//		getConversation(sessionEvent.getFrom())
//				.requestReceived(message,sessionEvent.getFrom());
//		//ExceptionUtil.createRuntimeException("Method is not implemented");
//		
//	}
//
//	public void buzzReceived(SessionEvent arg0) {
//		log.debug("call the method buzzReceived");
//		
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//		
//	}
//
//	public void offlineMessageReceived(SessionEvent arg0) {
//		log.debug("call the method offlineMessageReceived");
//		
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//		
//	}
//
//	public void errorPacketReceived(SessionErrorEvent arg0) {
//		log.debug("call the method errorPacketReceived");
//		
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//		
//	}
//
//	public void inputExceptionThrown(SessionExceptionEvent arg0) {
//		log.warn("call the method inputExceptionThrown");
//		
//		//ExceptionUtil.createRuntimeException("Method is not implemented");		
//		
//	}
//
//	public void newMailReceived(SessionNewMailEvent arg0) {
//		log.warn("newMailReceived is not implemented");	
//		
//	}
//
//	public void notifyReceived(SessionNotifyEvent arg0) {
//		log.warn("notifyReceived is not implemented");		
//	}
//
//	public void contactRequestReceived(SessionEvent sessionEvent) {
//		log.debug("call the method contactRequestReceived not supported");
//		try{
//			this.yahooMessengerSession.addFriend(sessionEvent.getFrom(), friendGroupName);
//		}catch(IOException exp){
//			log.warn(String.format("Cannnot add friend: %s, to group : %s" + sessionEvent.getFrom(), friendGroupName), exp);
//		}
//		
//	}
//
//	public void contactRejectionReceived(SessionEvent arg0) {
//		log.debug("call the method contactRejectionReceived  not supported");
//		
//	}
//
//	public void conferenceInviteReceived(SessionConferenceEvent arg0) {
//		log.debug("call the method conferenceInviteReceived  not supported");
//	}
//
//	public void conferenceInviteDeclinedReceived(SessionConferenceEvent arg0) {
//		log.debug("call the method conferenceInviteDeclinedReceived  not supported");
//		
//	}
//
//	public void conferenceLogonReceived(SessionConferenceEvent arg0) {
//		log.debug("call the method conferenceLogonReceived  not supported");
//	}
//
//	public void conferenceLogoffReceived(SessionConferenceEvent arg0) {
//		log.debug("call the method conferenceLogoffReceived  not supported");
//		
//	}
//
//	public void conferenceMessageReceived(SessionConferenceEvent arg0) {
//		log.debug("call the method conferenceMessageReceived  not supported");
//	}
//
//	public void friendsUpdateReceived(SessionFriendEvent friendEvent) {
//		log.debug("call the method friendsUpdateReceived");
//		for(YahooUser friend : friendEvent.getFriends()){
//			this.fireBuddyStatusChange(friend.getId());
//		}
//	}
//
//	public void friendAddedReceived(SessionFriendEvent friendEvent) {
//		log.debug("call the method friendAddedReceived");
//		this.addMessengerUser(friendEvent.getFriend().getId(), true);
//		
//	}
//
//	public void friendRemovedReceived(SessionFriendEvent friendEvent) {
//		log.debug("call the method friendRemovedReceived");
//		this.removeMessengerUser(friendEvent.getFriend().getId(), true);		
//		
//	}
//
//	public void chatLogonReceived(SessionChatEvent arg0) {
//		log.debug("call the method chatLogonReceived  not supported");
//	}
//
//	public void chatLogoffReceived(SessionChatEvent arg0) {
//		log.debug("call the method chatLogoffReceived  not supported");
//	}
//
//	public void chatMessageReceived(SessionChatEvent arg0) {
//		log.debug("call the method chatMessageReceived  not supported");
//	}
//
//	public void chatUserUpdateReceived(SessionChatEvent arg0) {
//		log.debug("call the method chatUserUpdateReceived  not supported");
//	}
//
//	public void chatConnectionClosed(SessionEvent arg0) {
//		log.debug("call the method chatConnectionClosed  not supported");
//	}
//
//	public boolean reConnect() {
//		log.debug("call the method reConnect");
//		
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//	
//		return false;
//	}
//
//	public boolean ping() {
//		log.debug("call the method ping");
//		
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//
//		return false;
//	}
//
//	public boolean isConnected() {
//		log.debug("call the method isConnected");
//		ExceptionUtil.createRuntimeException("Method is not implemented");
//		return false;
//	}
//
//	public BuddyStatusEnum getBuddyStatus(String messengerID) {
//		log.debug("call the method getBuddyStatus");
//		YahooUser yahooUser = yahooMessengerSession.getUser(messengerID);
//		
//		if(yahooUser == null){
//			return BuddyStatusEnum.notexisting;
//		}
//		
//		long status = yahooUser.getStatus();
//		
//		if(status == StatusConstants.STATUS_AVAILABLE){
//			return BuddyStatusEnum.online_available;
//		}
//		
//		if((status == StatusConstants.STATUS_OFFLINE) ||
//				(status == StatusConstants.STATUS_INVISIBLE) ||
//				(status == StatusConstants.STATUS_ONVACATION) ||
//				(status == StatusConstants.STATUS_BAD)){
//			return BuddyStatusEnum.offline;
//		}
//		
//		if((status == StatusConstants.STATUS_IDLE)||
//				(status == StatusConstants.STATUS_NOTATDESK) ||
//				(status == StatusConstants.STATUS_BRB) ||
//				(status == StatusConstants.STATUS_NOTINOFFICE) ||
//				(status == StatusConstants.STATUS_NOTATHOME) ||
//				(status == StatusConstants.STATUS_OUTTOLUNCH)||
//				(status == StatusConstants.STATUS_STEPPEDOUT)){
//			return BuddyStatusEnum.online_away;
//		}
//		
//		if((status == StatusConstants.STATUS_ONPHONE)||
//				(status == StatusConstants.STATUS_BUSY) ||
//				(status == StatusConstants.STATUS_CUSTOM)){
//			return BuddyStatusEnum.online_busy;
//		}
//		log.warn(String.format("The Yahoo messenger user (%s) has an unknown status code (%s)", messengerID, status));
//		return BuddyStatusEnum.unknown;
//	}
//
//	public void startActiveConversation(String userName) throws StartActiveWindowException {
//		//throw new StartActiveConversationException("YMSG doesn't support active conversation");
//		this.getConversation(userName);
//		//return null;
//	}
//
//	public void internalWindowStopped(IMessengerWindow conversation) {
//		YMSGConversation ymsgConversation = (YMSGConversation)conversation;
//		this.conversationMap.remove(ymsgConversation.getUserName());
//		
//	}



}
