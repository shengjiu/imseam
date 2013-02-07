package com.imseam.test.connector.netty;

import com.imseam.test.Connection;
import com.imseam.test.Message;
import com.imseam.test.RemoteInvocation;
import com.imseam.test.connector.netty.invocation.StartChatRemoteInvocation;
import com.imseam.test.message.AcceptInvitationMessage;
import com.imseam.test.message.BuddyAddedToWindowMessage;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.SignOutMessage;
import com.imseam.test.message.StatusChangeMessage;
import com.imseam.test.message.TextMessage;
import com.imseam.test.message.WindowClosedMessage;

public class RPCConnection implements Connection {
	
	private String username = null;
	
	public RPCConnection(String username){
		this.username = username;
	}

//	@Override
//	public List<String> getBuddyList() {
//		RemoteInvocation remoteInvocation = new GetBuddyListRemoteInvocation(username);
//		Object buddyListObject = NettyClientManager.instance().remoteCall(username, remoteInvocation, null);
//		if(buddyListObject != null){
//			return (List<String>) buddyListObject;
//		}
//		return null;
//	}

	@Override
	public String startChat(String... buddies) {

		RemoteInvocation remoteInvocation = new StartChatRemoteInvocation(username, buddies);
		Object windowIdObject = NettyClientManager.instance().remoteCall(username, remoteInvocation, null);
		if(windowIdObject != null){
			return (String)windowIdObject;
		}
		return null;
	}

	@Override
	public void acceptInvitation(String buddy) {
		Message message = new AcceptInvitationMessage(username, buddy);
		NettyClientManager.instance().sendMessage(username, message);
	}

	@Override
	public void invite(String buddy) {
		Message message = new InvitationMessage(username, buddy);
		NettyClientManager.instance().sendMessage(username, message);
	}

//	@Override
//	public String getBuddyStatus(String buddy) {
//		RemoteInvocation remoteInvocation = new GetBuddyStatusRemoteInvocation(username, buddy);
//		Object statusObject = NettyClientManager.instance().remoteCall(username, remoteInvocation, null);
//		if(statusObject != null){
//			return (String) statusObject;
//		}
//		return null;
//	}

	@Override
	public void setSelfStatus(String status) {
		Message message = new StatusChangeMessage(username, status);
		NettyClientManager.instance().sendMessage(username, message);

	}

//	@Override
//	public String getSelfStatus() {
//		RemoteInvocation remoteInvocation = new GetSelfStatusRemoteInvocation(username);
//		Object statusObject = NettyClientManager.instance().remoteCall(username, remoteInvocation, null);
//		if(statusObject != null){
//			return (String) statusObject;
//		}
//		return null;	
//	}

//	@Override
//	public String getSelfStatusForBuddy(String buddy) {
//		RemoteInvocation remoteInvocation = new GetSelfStatusForBuddyRemoteInvocation(username, buddy);
//		Object statusObject = NettyClientManager.instance().remoteCall(username, remoteInvocation, null);
//		if(statusObject != null){
//			return (String) statusObject;
//		}
//		return null;		
//	}

//	@Override
//	public void setSelfStatusForBuddy(String buddy, String status) {
//		Message message = new SetSelfStatusForBuddyMessage(username, buddy, status);
//		NettyClientManager.instance().sendMessage(username, message);
//	}

	@Override
	public void signOut() {
		Message message = new SignOutMessage(username, null);
		NettyClientManager.instance().sendMessage(username, message);
	}


	@Override
	public void sendMsg(String windowId, String msg) {
		Message message = new TextMessage(msg, username, windowId);
		NettyClientManager.instance().sendMessage(username, message);		
		
//		System.out.println("send message, Username: " + username +", message: " + msg);
		 
	}
	@Override
	public void closeWindow(String windowId){
		Message message = new WindowClosedMessage( username, windowId);
		NettyClientManager.instance().sendMessage(username, message);		
	}

	@Override
	public void addBuddiesToWindow(String windowId, String... buddies) {
		Message message = new BuddyAddedToWindowMessage(username, windowId, buddies);
		NettyClientManager.instance().sendMessage(username, message);

		
	}

}
