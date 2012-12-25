package com.imseam.connector.msn.jml;

import java.util.HashMap;
import java.util.Map;

import net.sf.jml.Email;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnFileTransfer;
import net.sf.jml.MsnGroup;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnAdapter;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.jml.message.MsnControlMessage;
import net.sf.jml.message.MsnDatacastMessage;
import net.sf.jml.message.MsnEmailActivityMessage;
import net.sf.jml.message.MsnEmailInitMessage;
import net.sf.jml.message.MsnEmailNotifyMessage;
import net.sf.jml.message.MsnInstantMessage;
import net.sf.jml.message.MsnSystemMessage;
import net.sf.jml.message.MsnUnknownMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rath.msnm.SwitchboardSession;
import rath.msnm.entity.MsnFriend;

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

public class JMLConnection extends AbstractConnection  {

	private static Log log = LogFactory.getLog(JMLConnection.class);

	private MsnMessenger messenger;

	private Map<MsnSwitchboard, JMLMessengerWindow> conversationMap = new HashMap<MsnSwitchboard, JMLMessengerWindow>();

	public JMLConnection(IChatletApplication application,
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


		messenger = MsnMessengerFactory.createMsnMessenger(this.getHostUserId(), this.getPassword());

		
		///default init status is online, 
        messenger.getOwner().setInitStatus(MsnUserStatus.ONLINE);

        //log incoming message
//        messenger.setLogIncoming(true);

        //log outgoing message
//        messenger.setLogOutgoing(true);
        
        JmlMsnAdapter listener = new JmlMsnAdapter();
//        messenger.addListener(new JmlMsnAdapter());
        
        messenger.addMessengerListener(listener);
        messenger.addContactListListener(listener);
//        messenger.addFriend(Email.parseStr("wangshengjiu@hotmail.com"), "shengjiu");
        messenger.addSwitchboardListener(listener);
        messenger.addMessageListener(listener);

	}

	
	public boolean connect() {
		try {
			messenger.login();
		} catch (Exception e) {
			ExceptionUtil
					.wrapRuntimeException(String.format("Cannot connect to MSN using JML SDK(user: %s, Password: %s",
											this.getHostUserId(), this.getPassword()), e);
		}
		return true;
	}
	
	

	public boolean reConnect() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void disconnecting() {
		messenger.logout();
		conversationMap.clear();
	}

	public boolean ping() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public BuddyStatusEnum getBuddyStatus(String email) {
		MsnContact friend = messenger.getContactList().getContactByEmail(Email.parseStr(email));
		if(friend == null){
			return BuddyStatusEnum.notexisting;
		}
		MsnUserStatus status = friend.getStatus();
		if(status.equals(MsnUserStatus.BUSY)||
				status.equals(MsnUserStatus.ON_THE_PHONE)){
			return BuddyStatusEnum.online_busy;
		}
		
		if(status.equals(MsnUserStatus.BE_RIGHT_BACK) ||
				status.equals(MsnUserStatus.AWAY) ||
						status.equals(MsnUserStatus.IDLE) ||
								status.equals(MsnUserStatus.OUT_TO_LUNCH)){
			return BuddyStatusEnum.online_away;
		}
		
		if(status.equals(MsnUserStatus.ONLINE)){
			return BuddyStatusEnum.online_available;
		}
		
		if(status.equals(MsnUserStatus.OFFLINE)||
				status.equals(MsnUserStatus.HIDE)){
			return BuddyStatusEnum.offline;
		}

		return BuddyStatusEnum.notexisting;
	}

	public MsnMessenger getJMSNMessenger() {
		return this.messenger;
	}

	private JMLMessengerWindow getMessengerWindow(MsnSwitchboard session) {
		assert (session != null);
		//session.
		JMLMessengerWindow conversation = this.conversationMap.get(session);
		if (conversation == null) {
			conversation = new JMLMessengerWindow(session, this);
			this.conversationMap.put(session, conversation);
			fireWindowStarted(conversation);
		}
		
		return conversation;
	}

	public void internalWindowStopped(IMessengerWindow conversation) {
		JMLMessengerWindow jmsnConversation = (JMLMessengerWindow)conversation;
		this.conversationMap.remove(jmsnConversation.getMsnSwitchboard());
		
	}
	public boolean isConnected() {
		ExceptionUtil.createRuntimeException("Method is not implemented");
		return false;
	}

	public void progressTyping(SwitchboardSession session, MsnFriend friend, String reason) {
		
		log.debug("EVENT: progressTyping" + friend.getLoginName() + " Reason: " + reason);
	}
	
	private String getFriendList(MsnSwitchboard session){
		MsnContact[] friends = session.getAllContacts();
		String friendStr = "";
		for(MsnContact contact: friends){
			friendStr += contact.getEmail().getEmailAddress() + " ";
		}
		return friendStr;
	}
	
	@Override
	public void startActiveWindow(String buddyUid,
			IStartActiveWindowCallback callback)
			throws StartActiveWindowException {
		try{
			String userName = UidHelper.parseUseridFromBuddyUid(buddyUid);
			if(this.getBuddyStatus(userName) == BuddyStatusEnum.online_available){
				//need to find examples 
				messenger.newSwitchboard(userName);
			}else{
				callback.startWindowFailed(new StartActiveWindowException(String.format("Active conversation can only be started when the messenger user's (%s) current status is online_available!", buddyUid), buddyUid));
			}
		}catch(Exception ioExp){
			callback.startWindowFailed(new StartActiveWindowException(String.format("Call active conversation for user (%s) IO error", buddyUid), ioExp, buddyUid));
		}
	}	
	
	
	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		try {
			Email email = Email.parseStr(userId);
			messenger.addFriend(email, userId);
		} catch (Exception e) {
			throw new InviteBuddyException(Reason.unknown, e);
		}
		
		
	}

	@Override
	public boolean isInviteBuddySupported() {
		return true;
	}
	
	
	private class JmlMsnAdapter extends MsnAdapter{

		@Override
		public void activityEmailNotificationReceived(MsnSwitchboard arg0, MsnEmailActivityMessage arg1, MsnContact arg2) {
			// TODO Auto-generated method stub
			super.activityEmailNotificationReceived(arg0, arg1, arg2);
		}

//		@Override
//		public void contactAddCompleted(MsnMessenger messenger, MsnContact contact) {
//			log.info("EVENT: listAdd (" + contact.getEmail() + ")");
//			fireBuddyAdded(contact.getId());
//			super.contactAddCompleted(messenger, contact);
//	    }
		
//		public void contactAddCompleted(MsnMessenger newMessenger, MsnContact contact) {
//			log.info("EVENT: listAdd (" + contact.getEmail() + ")");
//			fireBuddyAdded(contact.getId());
//			super.contactAddCompleted(newMessenger, contact);
//		}

		@Override
		public void contactAddedMe(MsnMessenger newMessenger, MsnContact contact) {
			log.info("**EVENT: whoAddedMe (" + contact.getId() + ")");
			try{
				messenger.addFriend(contact.getEmail(), contact.getFriendlyName());
				addMessengerUser(contact.getEmail().getEmailAddress(), true);
				super.contactAddedMe(newMessenger, contact);
			}catch(Exception ioe){
				log.info("error to add a friend: " + contact.getEmail().getEmailAddress());
			}
			
		}

		@Override
		public void contactJoinSwitchboard(MsnSwitchboard session, MsnContact contact) {
			getMessengerWindow(session).userJoin(contact.getEmail().getEmailAddress());
			log.debug(String.format("**EVENT: whoJoinSession: old session(%s), new friend(%s)", getFriendList(session), contact.getEmail()));

			super.contactJoinSwitchboard(session, contact);
		}

		@Override
		public void contactLeaveSwitchboard(MsnSwitchboard session, MsnContact contact) {
			getMessengerWindow(session).userLeave(contact.getEmail().getEmailAddress());
			log.debug(String.format("**EVENT: whoPartSession: old session(%s), new friend(%s)", getFriendList(session), contact.getEmail().getEmailAddress()));

			super.contactLeaveSwitchboard(session, contact);
		}

		@Override
		public void contactListInitCompleted(MsnMessenger arg0) {
			MsnContact[] contacts = messenger.getContactList().getContacts();
			for(MsnContact contact : contacts )
			{
				addMessengerUser(contact.getEmail().getEmailAddress(), false);
			}
			connectionStarted();

			super.contactListInitCompleted(arg0);
		}

		@Override
		public void contactListSyncCompleted(MsnMessenger arg0) {
			log.info("**EVENT: allListSynchronized");
			super.contactListSyncCompleted(arg0);
		}

//		@Override
//		public void contactRemoveCompleted(MsnMessenger arg0, MsnContact arg1) {
//			// TODO Auto-generated method stub
//			super.contactRemoveCompleted(arg0, arg1);
//		}

		@Override
		public void contactRemovedMe(MsnMessenger newMessenger, MsnContact contact) {
			log.info("**EVENT: whoRemovedMe (" + contact.getEmail() + ")");
			
			removeMessengerUser(contact.getEmail().getEmailAddress(), true);

			super.contactRemovedMe(newMessenger, contact);
		}

		@Override
		public void contactStatusChanged(MsnMessenger newMessenger, MsnContact contact) {
			fireBuddyStatusChange(contact.getEmail().getEmailAddress());
			log.debug("**EVENT: status changed (" + contact.getEmail() + "): " + contact.getStatus());
			super.contactStatusChanged(newMessenger, contact);
		}

		@Override
		public void controlMessageReceived(MsnSwitchboard arg0, MsnControlMessage message, MsnContact arg2) {
			log.info("EVENT: controlMessageReceived, " + message.toString());
			super.controlMessageReceived(arg0, message, arg2);
		}

		@Override
		public void datacastMessageReceived(MsnSwitchboard arg0, MsnDatacastMessage message, MsnContact arg2) {
			log.info("EVENT: datacastMessageReceived, " + message.toString());
			super.datacastMessageReceived(arg0, message, arg2);
		}

		@Override
		public void exceptionCaught(MsnMessenger arg0, Throwable exp) {
			log.warn("Jml got exceptions:", exp);
			super.exceptionCaught(arg0, exp);
		}

		@Override
		public void fileTransferFinished(MsnFileTransfer arg0) {
			log.debug(String.format("**Event: fileTransferFinished"));
			super.fileTransferFinished(arg0);
		}

		@Override
		public void fileTransferProcess(MsnFileTransfer arg0) {
			log.debug(String.format("**Event: fileTransferProcess"));
			super.fileTransferProcess(arg0);
		}

		@Override
		public void fileTransferRequestReceived(MsnFileTransfer arg0) {
			log.debug(String.format("**Event: fileTransferRequestReceived"));
			super.fileTransferRequestReceived(arg0);
		}

		@Override
		public void fileTransferStarted(MsnFileTransfer arg0) {
			log.debug(String.format("**Event: fileTransferStarted"));
			super.fileTransferStarted(arg0);
		}

		@Override
		public void groupAddCompleted(MsnMessenger arg0, MsnGroup group) {
			log.info("EVENT: groupRemoveCompleted, " + group.toString());
			super.groupAddCompleted(arg0, group);
		}

		@Override
		public void groupRemoveCompleted(MsnMessenger arg0, MsnGroup group) {
			log.info("EVENT: groupRemoveCompleted, " + group.toString());
			super.groupRemoveCompleted(arg0, group);
		}

		@Override
		public void initialEmailNotificationReceived(MsnSwitchboard arg0, MsnEmailInitMessage emailMessage, MsnContact arg2) {
			log.info("EVENT: initialEmailNotificationReceived, " + emailMessage.toString());
			super.initialEmailNotificationReceived(arg0, emailMessage, arg2);
		}

		@Override
		public void instantMessageReceived(MsnSwitchboard session, MsnInstantMessage mime, MsnContact contact) {
			log.debug("EVENT: messageReceived" + mime.getContent());

			MessengerTextMessage message = new MessengerTextMessage(mime
					.getContent());
			getMessengerWindow(session)
					.requestReceived(message, contact.getEmail().getEmailAddress());
			super.instantMessageReceived(session, mime, contact);
		}

		@Override
		public void loginCompleted(MsnMessenger newMessager) {
			
			log.debug("**EVENT: loginComplete (" + newMessager.getOwner().getEmail() + ")");
			super.loginCompleted(newMessager);
		}

		@Override
		public void logout(MsnMessenger arg0) {
			log.info("**EVENT: logoutNotify");
			super.logout(arg0);
		}

		@Override
		public void newEmailNotificationReceived(MsnSwitchboard arg0, MsnEmailNotifyMessage emailMessage, MsnContact arg2) {
			log.info("EVENT: newEmailNotificationReceived, " + emailMessage.getFromAddr());
			super.newEmailNotificationReceived(arg0, emailMessage, arg2);
		}

		@Override
		public void ownerStatusChanged(MsnMessenger newMessenger) {
			log.info("EVENT: ownerStatusChanged" + newMessenger.getOwner().getStatus());
			super.ownerStatusChanged(newMessenger);
		}

		@Override
		public void switchboardClosed(MsnSwitchboard session) {
			fireWindowStopped(getMessengerWindow(session));
//			conversationMap.remove(session.getSessionId());
			log.info("EVENT: sessionEnded ("
					+ getFriendList(session)
					+  ")");
			super.switchboardClosed(session);
		}

		@Override
		public void switchboardStarted(MsnSwitchboard session) {
			getMessengerWindow(session);
			
			log.info("EVENT: sessionOpened ("
					+ session.getAllContacts()[0].getEmail() + ","
					+ session.getAllContacts()[0].getStatus() + ")");
			super.switchboardStarted(session);
		}

		@Override
		public void systemMessageReceived(MsnMessenger arg0, MsnSystemMessage arg1) {
			// TODO Auto-generated method stub
			super.systemMessageReceived(arg0, arg1);
		}

		@Override
		public void unknownMessageReceived(MsnSwitchboard arg0, MsnUnknownMessage arg1, MsnContact arg2) {
			// TODO Auto-generated method stub
			super.unknownMessageReceived(arg0, arg1, arg2);
		}
		
	}
	
}
