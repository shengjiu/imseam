package com.imseam.chat.connector.jmsn;

import rath.msnm.MSNMessenger;
import rath.msnm.SwitchboardSession;
import rath.msnm.entity.MsnFriend;
import rath.msnm.event.MsnAdapter;
import rath.msnm.msg.MimeMessage;

public class Adapter extends MsnAdapter {

	MSNMessenger msn;
	
	SwitchboardSession session;

	public Adapter(MSNMessenger msn) {
		this.msn = msn;
	}

	public void switchboardSessionStarted( SwitchboardSession ss )
	{
		MsnFriend friend= ss.getLastFriend();
		
		session = ss;
		
		try {
			// create a new message
			MimeMessage mm = new MimeMessage();

			// remember to append the

			// trail to
			// your message
			mm.setMessage("How are you doing?");
			// set the message kind to MESSAGE -_-!!!
			// you have to do this.
			mm.setKind(mm.KIND_MESSAGE);

			if (msn.sendMessage(mm, ss)) {
				// message sent successfully
				System.out.println("send msg ok!");
			} else {
				// failed
				System.out.println("send msg faild!");
				
			}
			for(Object fdo:ss.getMsnFriends()){
				MsnFriend fd = (MsnFriend)fdo;
				System.out.println("instantMessageReceived include friend:" + fd.getLoginName());
			}
			System.out.println("switchboardSessionStarted :" + ss.getSessionId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void instantMessageReceived(SwitchboardSession ss, MsnFriend friend,
			MimeMessage mime) {
		System.out.println(friend.getFriendlyName() + " send me some msg:"
				+ mime.getMessage());
		sendTextMessage(ss, "http://www.google.com");
		sendTextMessage(ss, "I love you");
		for(Object fdo:ss.getMsnFriends()){
			MsnFriend fd = (MsnFriend)fdo;
			System.out.println("instantMessageReceived include friend:" + fd.getLoginName());
		}
		System.out.println("instantMessageReceived :" + ss.getSessionId());
		try{
			ss.inviteFriend("wangshengjiu@hotmail.com");
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
	@Override
	public void whoAddedMe( MsnFriend friend )
	{
		System.out.println("who added me" + friend.getFriendlyName());
		try {
			msn.addFriend(friend.getLoginName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void sendTextMessage(SwitchboardSession ss, String message){
		try {
			// create a new message
			MimeMessage mm = new MimeMessage();

			// remember to append the

			// trail to
			// your message
			mm.setMessage(message);
			
			// set the message kind to MESSAGE -_-!!!
			// you have to do this.
			mm.setKind(mm.KIND_MESSAGE);
			//mm.setFontColor(new Color(234, 243, 255));

			if (msn.sendMessage(mm, ss)) {
				// message sent successfully
				System.out.println("send msg ok!");
			} else {
				// failed
				System.out.println("send msg faild!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listAdd( MsnFriend friend )
	{
		System.out.println("listAdd: " + friend.getLoginName());
	}

	/**
	 * ????? ?? ??, ?????? ?????????? ?????????, ??????
	 * Contact list?? ???? ????????? ?????? Online(????? substate)??
	 * ?????????? ?? ??????? ???? ?????? ????? ????????.
	 * ???????? ???? ????????? ???????? ?? ?? ?????? ?????.
	 * <p>
	 * ??? Online Contact list?? ?????? ??????, MsnFriend ?????
	 * Map?? ??????????? ???????. (Key???? loginName???? ???? ?? ???)
	 */
	public void listOnline( MsnFriend friend )
	{
		System.out.println("listOnline: " + friend.getLoginName());
	}

	/**
	 * ?????? ContactList?? ???? ?????? ??? ????? ???????? ?????
	 * ?????? ????????????, NS?????? ???????? ?????????.
	 */
	public void userOnline( MsnFriend friend )
	{
		System.out.println("userOnline: " + friend.getLoginName());
	}

	/**
	 * ?????? Foward ContactList?? ???? ???????? ????????????? ????????
	 * ???????????? ?????? ??????????? NS?????? ???????? ?????????.
	 */
	public void userOffline( String loginName )
	{
		System.out.println("userOffline: " + loginName);
	}
}
