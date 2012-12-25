package com.imseam.connector.msn.jmsn;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rath.msnm.BuddyList;
import rath.msnm.MSNMessenger;
import rath.msnm.SwitchboardSession;
import rath.msnm.UserStatus;
import rath.msnm.entity.MsnFriend;
import rath.msnm.event.MsnListener;
import rath.msnm.ftp.VolatileDownloader;
import rath.msnm.ftp.VolatileTransferServer;
import rath.msnm.msg.MimeMessage;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.InviteBuddyException.Reason;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.chatlet.MessengerTextMessage;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.core.AbstractConnection;

public class JMSNConnection extends AbstractConnection implements MsnListener {

	private static Log log = LogFactory.getLog(JMSNConnection.class);

	private MSNMessenger msn;

	private Map<String, JMSNMessengerWindow> conversationMap = new HashMap<String, JMSNMessengerWindow>();

	public JMSNConnection(IChatletApplication application,
			ConnectionConfig config) {
		super(application, config);
	}

	public void initialize() {

		if (StringUtil.isNullOrEmptyAfterTrim(this.getHostUserId())) {
			ExceptionUtil
					.createRuntimeException("\'email\' is not defined. The JMSN Connection requires \'email\' to be defined in the init-params.");
		}

		if (StringUtil.isNullOrEmptyAfterTrim( this.getPassword())) {
			ExceptionUtil
					.createRuntimeException("\'password\' is not defined. The JMSN Connection requires \'password\' to be defined in the init-params.");
		}


		msn = new MSNMessenger(this.getHostUserId(), this.getPassword());
		// set the initial status to online.
		msn.setInitialStatus(UserStatus.ONLINE);
		// register your pre-created adapter to msn
		msn.addMsnListener(this);
		
//		Debug.printOutput = true;
//		Debug.printInput = true;
//		Debug.printMime = true;
		

	}

	
	public boolean connect() {
		try {
			msn.login();
		} catch (Exception e) {
			ExceptionUtil
					.wrapRuntimeException(String.format("Cannot connect to MSN using JMSN SDK(user: %s, Password: %s",
											this.getHostUserId(), this.getPassword()), e);
		}
		return true;
	}
	
	

	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void disconnecting() {
		msn.logout();
		conversationMap.clear();
	}

	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		MsnFriend friend = msn.getBuddyGroup().getForwardList().get(messengerID);
		if(friend == null){
			return BuddyStatusEnum.notexisting;
		}
		String status = friend.getStatus();
		if(status.equals(UserStatus.BUSY)||
				status.equals(UserStatus.ON_THE_PHONE)){
			return BuddyStatusEnum.online_busy;
		}
		
		if(status.equals(UserStatus.BE_RIGHT_BACK) ||
				status.equals(UserStatus.AWAY_FROM_COMPUTER) ||
						status.equals(UserStatus.IDLE) ||
								status.equals(UserStatus.ON_THE_LUNCH)){
			return BuddyStatusEnum.online_away;
		}
		
		if(status.equals(UserStatus.ONLINE)){
			return BuddyStatusEnum.online_available;
		}
		
		if(status.equals(UserStatus.OFFLINE)||
				status.equals(UserStatus.INVISIBLE)){
			return BuddyStatusEnum.offline;
		}

		return BuddyStatusEnum.notexisting;
	}

	public MSNMessenger getJMSNMessenger() {
		return this.msn;
	}

	private JMSNMessengerWindow getConversation(SwitchboardSession session) {
		assert (session != null);
		//session.
		JMSNMessengerWindow conversation = this.conversationMap.get(session.getSessionId());
		if (conversation == null) {
			conversation = new JMSNMessengerWindow(session, this);
			this.conversationMap.put(session.getSessionId(), conversation);
			fireWindowStarted(conversation);
		}
		
		return conversation;
	}

	public void internalWindowStopped(IMessengerWindow conversation) {
		JMSNMessengerWindow jmsnConversation = (JMSNMessengerWindow)conversation;
		this.conversationMap.remove(jmsnConversation.getID());
		
	}
	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void switchboardSessionStarted(SwitchboardSession session) {
		getConversation(session);
		
		log.info("EVENT: sessionOpened ("
				+ session.getLastFriend().getLoginName() + ","
				+ session.getLastFriend().getStatus() + ")");
	}
	
	public void switchboardSessionEnded(SwitchboardSession session) {
		this.fireWindowStopped(getConversation(session));
//		conversationMap.remove(session.getSessionId());
		log.info("EVENT: sessionEnded ("
				+ session.getLastFriend().getLoginName() + ","
				+ session.getLastFriend().getStatus() + ")");
		
	}

	public void switchboardSessionAbandon(SwitchboardSession session, String reason) {
		this.fireWindowStopped(getConversation(session));
//		conversationMap.remove(session.getSessionId());
		log.info("EVENT: SessionAbandon ("
				+ session.getLastFriend().getLoginName() + ","
				+ session.getLastFriend().getStatus() + "), Reason: " 
				+ reason);
		
	}

	public void progressTyping(SwitchboardSession session, MsnFriend friend, String reason) {
		
		log.debug("EVENT: progressTyping" + friend.getLoginName() + " Reason: " + reason);
	}

	public void instantMessageReceived(SwitchboardSession session,
			MsnFriend friend, MimeMessage mime) {

		log.debug("EVENT: messageReceived" + mime.getMessage());

		MessengerTextMessage message = new MessengerTextMessage(mime
				.getMessage());
		getConversation(session)
				.requestReceived(message, friend.getLoginName());

	}

	public void loginComplete(MsnFriend owner) {
		BuddyList bl = msn.getBuddyGroup().getForwardList();
		for(@SuppressWarnings("rawtypes")Iterator i=bl.iterator(); i.hasNext(); )
		{
			MsnFriend friend = (MsnFriend)i.next();
			this.addMessengerUser(friend.getLoginName(), false);
		}
		this.connectionStarted();
		
		log.debug("**EVENT: loginComplete (" + owner.getLoginName() + ")");
	}

	public void loginError(String error) {
		log.debug("**EVENT: loginError (" + error + ")");
	}

	public void listAdd(MsnFriend friend) {
		log.info("EVENT: listAdd (" + friend.getLoginName() + ")");
		this.fireBuddyAdded(friend.getLoginName());
	}

	public void listOnline(MsnFriend friend) {
		log.debug("**EVENT: listOnline (" + friend.getLoginName() + ")");
		this.fireBuddyStatusChange(friend.getLoginName());
		
	}

	public void userOnline(MsnFriend friend) {
		this.fireBuddyStatusChange(friend.getLoginName());
		log.debug("**EVENT: userOnline (" + friend.getLoginName() + ")");
	}

	public void userOffline(String loginName) {
		this.fireBuddyStatusChange(loginName);
		log.debug("**EVENT: userOffline (" + loginName + ")");
	}

	public void whoJoinSession(SwitchboardSession session, MsnFriend friend) {
		this.getConversation(session).userJoin(friend.getLoginName());
		log.debug(String.format("**EVENT: whoJoinSession: old session(%s), new friend(%s)", getFriendList(session), friend.getLoginName()));
	}
	
	private String getFriendList(SwitchboardSession session){
		@SuppressWarnings("rawtypes")
		Collection friends = session.getMsnFriends();
		String friendStr = "";
		for(Object object : friends){
			MsnFriend friend = (MsnFriend)object;
			friendStr += friend.getLoginName() + " ";
		}
		return friendStr;
	}

	public void whoPartSession(SwitchboardSession session, MsnFriend friend) {
		this.getConversation(session).userLeave(friend.getLoginName());
		log.debug(String.format("**EVENT: whoPartSession: old session(%s), new friend(%s)", getFriendList(session), friend.getLoginName()));
	}



	public void filePosted(SwitchboardSession session, int arg1, String arg2, int arg3) {
		log.debug(String.format("**Event: filePosted"));
		
	}

	public void fileSendAccepted(SwitchboardSession arg0, int arg1) {
		log.debug(String.format("**Event: fileSendAccepted"));
		
	}

	public void fileSendRejected(SwitchboardSession arg0, int arg1, String arg2) {
		log.debug(String.format("**Event: fileSendRejected"));
		
	}

	public void fileSendStarted(VolatileTransferServer arg0) {
		log.debug(String.format("**Event: fileSendStarted"));
		
	}

	public void fileSendEnded(VolatileTransferServer arg0) {
		log.debug(String.format("**Event: fileSendEnded"));
		
	}

	public void fileReceiveStarted(VolatileDownloader arg0) {
		log.debug(String.format("**Event: fileReceiveStarted"));
		
	}

	public void fileSendError(VolatileTransferServer arg0, Throwable arg1) {
		log.debug(String.format("**Event: fileSendError"));
		
	}

	public void fileReceiveError(VolatileDownloader arg0, Throwable arg1) {
		log.debug(String.format("**Event: fileReceiveError"));
		
	}

	public void whoAddedMe(MsnFriend friend) {
		log.info("**EVENT: whoAddedMe (" + friend.getLoginName() + ")");
		try{
			msn.addFriend(friend.getLoginName());
			this.addMessengerUser(friend.getLoginName(), true);
		}catch(IOException ioe){
			log.info("error to add a friend: " + friend.getLoginName());
		}
		
	}

	public void whoRemovedMe(MsnFriend friend) {
		log.info("**EVENT: whoRemovedMe (" + friend.getLoginName() + ")");
		
		this.removeMessengerUser(friend.getLoginName(), true);
		
	}

	public void buddyListModified() {
		log.info("**EVENT: buddyListModified");
		
	}

	public void addFailed(int arg0) {
		log.info("**EVENT: addFailed" + arg0);
	}

	public void renameNotify(MsnFriend friend) {
		log.info("**EVENT: renameNotify (" + friend.getLoginName() + ")");
	}

	public void allListUpdated() {
		log.info("**EVENT: allListUpdated");
	}

	public void logoutNotify() {
		log.info("**EVENT: logoutNotify");
		
	}

	public void notifyUnreadMail(Properties arg0, int arg1) {
		log.info("**EVENT: notifyUnreadMail");
		
	}


	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		try {
			msn.addFriend(userId);
		} catch (IOException e) {
			throw new InviteBuddyException(Reason.unknown, e);
		}
	}

	@Override
	public boolean isInviteBuddySupported() {
		return true;
	}

	@Override
	public void startActiveWindow(String buddyUid,
			IStartActiveWindowCallback callback)
			throws StartActiveWindowException {
		try{
			String userName = UidHelper.parseUseridFromBuddyUid(buddyUid);
			if(this.getBuddyStatus(userName) == BuddyStatusEnum.online_available){
				this.msn.doCall(userName);
			}else{
				callback.startWindowFailed(new StartActiveWindowException(String.format("Active conversation can only be started when the messenger user's (%s) current status is online_available!", buddyUid), buddyUid));
			}
		}catch(IOException ioExp){
			callback.startWindowFailed(new StartActiveWindowException(String.format("Call active conversation for user (%s) IO error", buddyUid), ioExp, buddyUid));
		}
	}


}
