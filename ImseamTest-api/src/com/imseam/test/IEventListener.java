package com.imseam.test;

import com.imseam.test.message.AcceptInvitationMessage;
import com.imseam.test.message.BuddyAddedToWindowMessage;
import com.imseam.test.message.InvitationMessage;
import com.imseam.test.message.SignOutMessage;
import com.imseam.test.message.StatusChangeMessage;
import com.imseam.test.message.TextMessage;
import com.imseam.test.message.UserLoginMessage;
import com.imseam.test.message.WindowClosedMessage;
import com.imseam.test.message.WindowOpennedMessage;

public interface IEventListener {
	//not on client
	void onUserLogin(UserLoginMessage message);
	
	//not on server
	void onWindowOpened(WindowOpennedMessage message);
	
	void onWindowClosed(WindowClosedMessage message);
	
	void onBuddyAddedToWindow(BuddyAddedToWindowMessage message);
	
	void onTextMessage(TextMessage message);
	
	void onInvitation(InvitationMessage message);
	
	void onInvitationAccepted(AcceptInvitationMessage message);
	
	//not on client
	void onBuddyStatusChange(StatusChangeMessage message);
	
	//not on client
	void onBuddySignOut(SignOutMessage message);

}
