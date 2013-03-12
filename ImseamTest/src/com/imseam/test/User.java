package com.imseam.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.test.connector.netty.NettyClientManager;
import com.imseam.test.connector.netty.RPCConnector;
import com.imseam.test.connector.netty.invocation.WindowStartedRemoteInvocation;
import com.imseam.test.message.AcceptInvitationMessage;
import com.imseam.test.message.BuddyAddedToWindowMessage;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.SignOutMessage;
import com.imseam.test.message.StatusChangeMessage;
import com.imseam.test.message.TextMessage;
import com.imseam.test.message.UserLoginMessage;
import com.imseam.test.message.WindowClosedMessage;
import com.imseam.test.message.WindowOpennedMessage;

public class User {
	
	private Connection connection = null;
	
	private UserEventListener eventListener = new UserEventListener();
	
	private Map<String, Window> windowMap = new HashMap<String, Window>();
	
	private Map<String, BlockingQueue<Message>> receivedMessagesMap = new ConcurrentHashMap<String, BlockingQueue<Message>>(); 
	
	private static final String USER_RECEIVED_MESSAGES_KEY = "USER_RECEIVED_MESSAGES_KEY";
	
	private Connector connector;
	private String userId; 
	private String password; 
	private String status;
	
	public User(Connector connector, String userId, String password, String status){
		this.connector = connector; 
		this.userId = userId; 
		this.password = password; 
		this.status = status;
	}
	
	public void login(){
		connection = connector.login(userId, password, status, eventListener);
	}
	
	public void signOut(){
		assert(connection != null);
		connection.signOut();
	}
	
//	public List<String> getBuddyList(){
//		return connection.getBuddyList();
//	}
	
	Connection getConnection(){
		return connection;
	}
	public Window startChat(String ... buddies){
		String windowId = connection.startChat(buddies);
		 
		System.out.println("window started: " + windowId);
		this.receivedMessagesMap.put(windowId, new LinkedBlockingQueue<Message>());
		NettyClientManager.instance().addWindowEventListener(windowId, eventListener);
		 
		RemoteInvocation remoteInvocation = new WindowStartedRemoteInvocation(userId, windowId);
		NettyClientManager.instance().remoteCall(userId, remoteInvocation, null);
		return new Window(windowId, this, buddies);
	}
	
	private BlockingQueue<Message> getReceivedMessagsForWindow(String windowId){
		return this.receivedMessagesMap.get(windowId);
	}
	
	private BlockingQueue<Message> getReceivedMessagsForUser(){
		BlockingQueue<Message> receivedMessages = this.receivedMessagesMap.get(USER_RECEIVED_MESSAGES_KEY);
		
		if(receivedMessages == null){
			receivedMessages = new LinkedBlockingQueue<Message>();
			receivedMessagesMap.put(USER_RECEIVED_MESSAGES_KEY, receivedMessages);
		}
		
		return receivedMessages;
	} 
	
	public Window getWindow(String windowId){
		return this.windowMap.get(windowId);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Message> T waitForMessage(String windowId, int milliSeconds) throws WaitException{
		Message message = null;
		try {
			BlockingQueue<Message> receivedMessages = null;
			if(StringUtil.isNullOrEmptyAfterTrim(windowId)){
				receivedMessages = getReceivedMessagsForUser();
			}else{
				receivedMessages = getReceivedMessagsForWindow(windowId);
			}

			if(milliSeconds > 0){
				message = receivedMessages.poll(milliSeconds, TimeUnit.MILLISECONDS);
			}else{
				message = receivedMessages.take();
			}
			return (T) message;
		} catch (InterruptedException e) {
			throw new WaitException("Timeout or interrupted", e);
		}
	}

	public InvitationMessage waitForInvitation(int milliSeconds) throws WaitException{
		return waitForMessage(null, milliSeconds);
	}
	
	public void closeWindow(String windowId){
		connection.closeWindow(windowId);
		NettyClientManager.instance().removeWindowEventListener(windowId);
	}

	
	public AcceptInvitationMessage waitForInvitationAccepted(int milliSeconds) throws WaitException{
		return waitForMessage(null, milliSeconds);
	}
	
	public BuddyAddedToWindowMessage waitForBuddyAddedToWindow(String windowId, int milliSeconds) throws WaitException{
		return waitForMessage(windowId, milliSeconds);
	}
	
	public WindowOpennedMessage waitForWindowOpen(int milliSeconds) throws WaitException{
		return waitForMessage(null, milliSeconds);
	}

	public WindowClosedMessage waitForWindowClose(String windowId, int milliSeconds) throws WaitException{
		return waitForMessage(windowId, milliSeconds);
	}

	public TextMessage waitForTextMessage(String windowId, int milliSeconds) throws WaitException{
		return waitForMessage(windowId, milliSeconds);
	}

	
	public void acceptInvitation(String buddy){
		connection.acceptInvitation(buddy);
	}
	
	public void invite(String buddy){
		connection.invite(buddy);
	}
	
	
	public void setSelfStatus(String status){
		connection.setSelfStatus(status);
	}
	
	private class UserEventListener implements  IEventListener{
	
		@Override
		public void onWindowOpened(WindowOpennedMessage message) {
			Window window = new Window(message.windowId(), User.this, message.getBuddyIds());
			windowMap.put(window.windowId(), window);
			assert(getReceivedMessagsForWindow(message.windowId()) == null);
			BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<Message>();
			User.this.receivedMessagesMap.put(message.windowId(), receivedMessages);
			receivedMessages.add(message);
			NettyClientManager.instance().addWindowEventListener(message.windowId(), this);
		}
		
//		private String [] getArrrayFromSet(Set<String> buddyIdSet){
//			if (buddyIdSet == null) return new String[0];
//			return buddyIdSet.toArray(new String[buddyIdSet.size()]);
//		}
	
		@Override
		public void onTextMessage(TextMessage message) {
			
			getReceivedMessagsForWindow(message.windowId()).add(message);
			System.out.println("client received, window id: " + message.windowId() +", message target" +message.getTargetId() + ", "+message.getContent());
		}
	
		@Override
		public void onInvitation(InvitationMessage message) {
			getReceivedMessagsForUser().add(message);
			
		}

		@Override
		public void onBuddyAddedToWindow(BuddyAddedToWindowMessage message) {
			windowMap.get(message.windowId()).buddyAddedToWindow(message.getBuddyIds());
			getReceivedMessagsForWindow(message.windowId()).add(message);
		}

		@Override
		public void onInvitationAccepted(AcceptInvitationMessage message) {
			getReceivedMessagsForUser().add(message);
		}


		@Override
		public void onBuddyStatusChange(StatusChangeMessage message) {
			ExceptionUtil.createRuntimeException("buddy status should not received by user!");
		}

		@Override
		public void onBuddySignOut(SignOutMessage message) {
			ExceptionUtil.createRuntimeException("buddy sign out should not received by user!");
		}

		@Override
		public void onWindowClosed(WindowClosedMessage message) {
			getReceivedMessagsForUser().add(message);
			NettyClientManager.instance().removeWindowEventListener(message.windowId());
		}

		@Override
		public void onUserLogin(UserLoginMessage message) {
			ExceptionUtil.createRuntimeException("User login should not received by user!");
			
		}

	}
	
	
	public static void main(String [] args){
		User user = new User(new RPCConnector("localhost", 17001), "Test user", "server", Constants.online);
		user.login();
		Window window = user.startChat("test buddy 1");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true){
			System.out.print("User input? ");
	        String userInput = null;
	        try {
	        	userInput = reader.readLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
		    if("bye".equalsIgnoreCase(userInput)){
		    	break;
		    }
	
		    window.sendMsg(userInput);
		    
		    TextMessage serverOutput = null;
			try {
				serverOutput = window.waitForTextMessage(0);
			} catch (WaitException e) {
				e.printStackTrace();
			}
			System.out.println("Sever output:");
		    System.out.println(serverOutput.getContent());
		    System.out.println();
		}
	}
}
