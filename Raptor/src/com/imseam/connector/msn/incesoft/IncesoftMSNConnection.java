package com.imseam.connector.msn.incesoft;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.imseam.raptor.core.AbstractConnection;
import com.incesoft.botplatform.sdk.RobotException;
import com.incesoft.botplatform.sdk.RobotHandler;
import com.incesoft.botplatform.sdk.RobotMessage;
import com.incesoft.botplatform.sdk.RobotServer;
import com.incesoft.botplatform.sdk.RobotServerFactory;
import com.incesoft.botplatform.sdk.RobotSession;
import com.incesoft.botplatform.sdk.RobotUser;

public class IncesoftMSNConnection extends AbstractConnection implements
		RobotHandler {

	private static Log log = LogFactory.getLog(IncesoftMSNConnection.class);

	private RobotServer server;
	
	private String robotName = null;
	
	private Map<RobotSession, IncesoftMSNConversation> conversationMap = new HashMap<RobotSession, IncesoftMSNConversation>();

	public IncesoftMSNConnection(IChatletApplication application,
			ConnectionConfig config) {
		super(application, config);
	}

	public void initialize() {
		String hostAddr = config.get("sphost");
		robotName = config.get("robotname");
		if (StringUtil.isNullOrEmptyAfterTrim(hostAddr)) {
			ExceptionUtil.createRuntimeException("\'sphost\' is not defined. The Incesoft Connection requires \'sphost\' to be defined in the init-params.");
		}
		
		server = RobotServerFactory.getInstance().createRobotServer(
				hostAddr, 6602);
		server.setReconnectedSupport(true);
		server.setRobotHandler(this);
		
	}


	public boolean connect() {
		try {
			server.login(this.getHostUserId(), this.getPassword());
		} catch (RobotException e) {
			ExceptionUtil.wrapRuntimeException(String.format("Cannot connect to MSN using Incesoft SDK(user: %s, Password: %s", this.getHostUserId(), this.getPassword()), e);
		}
		return true;
	}

	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void disconnecting() {
		server.logout();

	}

	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public BuddyStatusEnum getBuddyStatus(String messengerID) {
//		server.
//		if(friend == null){
//			return BuddyStatusEnum.notexisting;
//		}
//		String status = friend.getStatus();
//		if(status.equals(UserStatus.BUSY)||
//				status.equals(UserStatus.ON_THE_PHONE)){
//			return BuddyStatusEnum.online_busy;
//		}
//		
//		if(status.equals(UserStatus.BE_RIGHT_BACK) ||
//				status.equals(UserStatus.AWAY_FROM_COMPUTER) ||
//						status.equals(UserStatus.IDLE) ||
//								status.equals(UserStatus.ON_THE_LUNCH)){
//			return BuddyStatusEnum.online_away;
//		}
//		
//		if(status.equals(UserStatus.ONLINE)){
//			return BuddyStatusEnum.online_available;
//		}
//		
//		if(status.equals(UserStatus.OFFLINE)||
//				status.equals(UserStatus.INVISIBLE)){
//			return BuddyStatusEnum.offline;
//		}

		return BuddyStatusEnum.online_available;
		
	}

	private IncesoftMSNConversation getConversation(RobotSession session){
		assert(session != null);
		IncesoftMSNConversation conversation = this.conversationMap.get(session);
		if(conversation == null){
			
			conversation = new IncesoftMSNConversation(session, this);
			this.conversationMap.put(session, conversation);
		}
		return conversation;
	}
	
	public void sessionOpened(RobotSession session) {
		if (RobotSession.OPEN_MODE_CONV_OPEN == session.getOpenMode()){
			this.fireWindowStarted(getConversation(session));
		}
		log.info("EVENT: sessionOpened ("
				+ session.getUser().getClientID() + ","
				+ session.getUser().getStatus() + ")");
	}

	public void sessionClosed(RobotSession session) {
		this.fireWindowStopped(getConversation(session));
//		conversationMap.remove(session);
	}

	public void messageReceived(RobotSession session, RobotMessage robotMessage) {

		log.debug("EVENT: messageReceived" +robotMessage.getString());
		
		MessengerTextMessage message = new MessengerTextMessage(robotMessage.getString());
		getConversation(session).requestReceived(message, session.getUser().getID());
		
	}

	public void nudgeReceived(RobotSession session) {
		log.debug("EVENT: nudgeReceived");
	}



	public void exceptionCaught(RobotSession session, Throwable cause) {
		log.warn("SERVER ERROR: ", cause);
	}

	public void userAdd(String robot, String user) {
		log.info("EVENT: userAdd (" + user + ")");
		this.fireBuddyAdded(user);
	}

	public void userRemove(String robot, String user) {
		log.info("EVENT: userRemove (" + user + ")");
		this.fireBuddyRemoved(user);
	}
	
	public void userJoined(RobotSession session, RobotUser user)  {
		this.getConversation(session).userJoin(user.getID());
	}

	public void userLeft(RobotSession session, RobotUser user) {
		this.getConversation(session).userLeave(user.getID());
	}

	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void startActiveConversation(String userName) throws StartActiveWindowException {

		try{
			this.server.createSession(robotName, userName);
		}catch(Exception exp){
//			throw new StartActiveWindowException("Free Incesoft cannot start the active conversation", exp);			
		}
	}

	public void internalWindowStopped(IMessengerWindow conversation) {
		IncesoftMSNConversation inceConversation = (IncesoftMSNConversation)conversation;
		this.conversationMap.remove(inceConversation.getRobotSession());
	}

	public void activityAccepted(RobotSession arg0) {
		log.debug("EVENT: activityAccepted");
	}

	public void activityRejected(RobotSession arg0) {
		log.debug("EVENT: activityRejected");
	}
	
	public void activityReceived(RobotSession arg0, String arg1) {
		log.debug("EVENT: activityReceived");
	}

	public void activityLoaded(RobotSession arg0) {
		log.debug("EVENT: activityLoaded");
		
	}

	public void activityClosed(RobotSession arg0) {
		log.debug("EVENT: activityClosed");
	}
	


	public void fileAccepted(RobotSession arg0) {
		log.debug("EVENT: fileAccepted");
	}

	public void fileRejected(RobotSession arg0) {
		log.debug("EVENT: fileRejected");
	}

	public void fileTransferEnded(RobotSession arg0) {
		log.debug("EVENT: fileTransferEnded");
	}

	public void backgroundAccepted(RobotSession arg0) {
		log.debug("EVENT: backgroundAccepted");
		
	}

	public void backgroundRejected(RobotSession arg0) {
		log.debug("EVENT: backgroundRejected");
	}

	public void backgroundTransferEnded(RobotSession arg0) {
		log.debug("EVENT: backgroundTransferEnded");
	}

	public void webcamAccepted(RobotSession arg0) {
		log.debug("EVENT: webcamAccepted");
	}

	public void webcamRejected(RobotSession arg0) {
		log.debug("EVENT: webcamRejected");
	}

	@Override
	public void fireBuddyStatusChange(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHostUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceId() {
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

}
