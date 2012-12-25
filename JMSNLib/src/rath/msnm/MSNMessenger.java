/*
 * @(#)MSNMessenger.java
 *
 * Copyright (c) 2001-2002, JangHo Hwang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	3. Neither the name of the JangHo Hwang nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *    $Id: MSNMessenger.java,v 1.29 2005/05/20 06:15:03 xrath Exp $
 */
package rath.msnm;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import rath.msnm.entity.MsnFriend;
import rath.msnm.entity.ServerInfo;
import rath.msnm.event.MsnAdapter;
import rath.msnm.event.MsnListener;
import rath.msnm.ftp.ToSendFile;
import rath.msnm.ftp.VolatileDownloader;
import rath.msnm.ftp.VolatileTransferServer;
import rath.msnm.msg.FileTransferMessage;
import rath.msnm.msg.MimeMessage;
import rath.msnm.util.BASE64;
/**
 * MSN ë©”ì‹ ? ¸ ?„œë²„ì— ? ‘?†?•˜ê³? ?´ê²ƒì??ê²ƒì„ ?•˜ê¸? ?œ„?•´
 * Entry pointê°? ?˜?Š” ?´?˜?Š¤?´?‹¤.
 * login?„ ?š”ì²??•˜ê³? loginComplete ?´ë²¤íŠ¸ê°? ë°œìƒ?œ ?›„ë¶??„°
 * ?´ê²ƒì??ê²? ë©”ì†Œ?“œë¥? ?‚¬?š©?•  ?ˆ˜ ?ˆ?‹¤.
 * ê·¸ë ‡ì§? ?•Š?œ¼ë©? NS proc ë¯¸ìƒ?„±?œ¼ë¡? NullPointerException?„ ë§Œë‚ ê²ƒì´?‹¤.
 * <p>
 * <pre><code>
 *  MSNMessenger msn = new MSNMessenger( "xiguel@hotmail.com", "12341234" );
 *  msn.setInitialStatus( UserStatus.ONLINE );
 *  msn.addMsnListener( new MsnAdapter() {
 *      public void progressTyping( SwitchboardSession ss,
 *          MsnFriend friend, String typingUser )
 *      {
 *          System.out.println( "Typing on " + friend.getLoginName() );
 *      }
 *      public void instantMessageReceived( SwitchboardSession ss,
 *          MsnFriend friend, MimeMessage mime )
 *      {
 *          System.out.println( "*** MimeMessage from " + friend.getLoginName() );
 *          System.out.println( mime.getMessage() );
 *          System.out.println( "*****************************" );
 *      }
 *  });
 *  msn.login();
 * </code></pre>
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: MSNMessenger.java,v 1.29 2005/05/20 06:15:03 xrath Exp $
 */
public class MSNMessenger
{	
	private NotificationProcessor ns = null;
	private BuddyGroup buddyGroup = null;
	private BuddyList forwardList = null;
	private LocalCopy localCopy = null;

	boolean isLogged = false;
	public boolean is911 = false;
	private byte[] bPhoto = null;
	private BufferedImage imgPhoto = null;
	private String ctxPhoto = null;

	private String loginName = null;
	private String password = null;
	private String initStatus = UserStatus.ONLINE;
	private MsnListener base = null;
	protected ArrayList listeners = new ArrayList();

	private MsnFriend owner = null;
	private Hashtable sessionMap = new Hashtable();

	/**
	 * MSNMessenger ê°ì²´ë¥? ?ƒ?„±?•œ?‹¤.
	 */
	public MSNMessenger()
	{
		this( null, null );
	}

	/**
	 * ì£¼ì–´ì§? account? •ë³´ë¡œ MSNMessenger ê°ì²´ë¥? ?ƒ?„±?•œ?‹¤.
	 *
	 * @param  loginName  ?‚¬?š©?•  login ?´ë¦?. (e.g. windrath@hotmail.com)
	 * @param  password   ??‹ ?˜ password
	 */
	public MSNMessenger( String loginName, String password )
	{
		this.loginName = loginName;
		this.password = password;
		this.owner = new MsnFriend(loginName);

		this.base = new Listener();
		this.buddyGroup = BuddyGroup.getInstance();
		this.forwardList = buddyGroup.getForwardList();
		this.localCopy = new LocalCopy();
	}

	private void initLogon()
	{
		buddyGroup.clear();
		localCopy.setLoginName( loginName );

		/* ìµœê·¼ ?‹œë¦¬ì–¼?„˜ë²„ë?? NS?— ?„¤? •?•œ?‹¤ */
		localCopy.loadInformation();
		ns.lastFrom = localCopy.getProperty("SerialFrom", "0");
		ns.lastTo   = localCopy.getProperty("SerialTo", "0");
		ns.lastFN   = localCopy.getProperty("FriendlyName", loginName);

		/* ìµœê·¼ ë²„ë”” ë¦¬ìŠ¤?Š¸ë¥? ê°?? ¸?˜¨?‹¤. */
		localCopy.loadBuddies( buddyGroup );
	}

	/**
	 * ?ƒˆë¡œìš´ ?‹œë¦¬ì–¼ ë²ˆí˜¸ë¥? ????¥?•œ ?›„, BuddyList?´ ë³?ê²½ë˜?—ˆ?‹¤?Š” ?´ë²¤íŠ¸ë¥? ë°œì†¡?•œ?‹¤.
	 *
	 * @param serial
	 * @param fireEvent
	 */
	void storeLocalCopy( String from, String to )
	{
		localCopy.setProperty( "SerialFrom", from );
		localCopy.setProperty( "SerialTo", to );
		localCopy.setProperty( "FriendlyName", owner.getFriendlyName() );
		localCopy.storeInformation();
		localCopy.storeBuddies( buddyGroup );

		fireBuddyListModifiedEvent();
	}

	public BuddyGroup getBuddyGroup()
	{
		return this.buddyGroup;
	}

	public LocalCopy getLocalCopy()
	{
		return this.localCopy;
	}

	/**
	 * ë¡œê·¸?¸ ?• ?•Œ?˜ ì´ˆê¸° ?ƒ?ƒœê°’ì„ ?„¤? •?•œ?‹¤. ê¸°ë³¸ê°’ìœ¼ë¡œëŠ” '?˜¨?¼?¸'?´?‹¤.
	 * ?´ê³³ì— ? ?š©?•  ?ˆ˜ ?ˆ?Š” ?ƒ?ƒœê°’ë“¤??? {@link UserStatus UserStatus} ?¸?„°?˜?´?Š¤?—
	 * ?„ ?–¸?˜?–´?ˆ?Š” ?ƒ?ˆ˜?“¤?„ ?‚¬?š©?•˜ë©? ?œ?‹¤.
	 */
	public void setInitialStatus( String code )
	{
		this.initStatus = code;
	}

	/**
	 * ?˜„?¬ ?„¤? •?œ ì´ˆê¸° ?ƒ?ƒœì½”ë“œë¥? ?–»?–´?˜¨?‹¤.
	 */
	public String getInitialStatus()
	{
		return this.initStatus;
	}

/*
	public void setMyPhoto( File file ) throws Exception
	{
		if( file==null )
		{
			imgPhoto = null;
			bPhoto = null;
			ctxPhoto = null;

			if( owner!=null )
			{
				owner.setPhotoContext( null );
				owner.setPhoto( null );
			}

			if( isLogged )
				ns.setMyStatus( owner.getStatus() );

			return;
		}

		PhotoFormatter format = new PhotoFormatter();

		this.imgPhoto = format.resize(file);
		this.bPhoto = format.getPNGBytes(imgPhoto);
		
		// Test code
		String sha1d = getPhotoSHA1D();
		String filename = getIdHash(loginName);
		String sha1c = getPhotoSHA1C(filename, sha1d);

		StringBuffer sb = new StringBuffer();
		sb.append( "<msnobj Creator=\"" );
		sb.append( loginName );
		sb.append( "\" Size=\"" );
		sb.append( bPhoto.length );
		sb.append( "\" Type=\"3\" Location=\"TFR" );
		sb.append( filename );
		sb.append( "\" Friendly=\"AAA=\" SHA1D=\"" );
		sb.append( sha1d );
		sb.append( "\" SHA1C=\"" );
		sb.append( sha1c );
		sb.append( "\"/>" );

		this.ctxPhoto = sb.toString();

		if( isLogged )
		{
			owner.setPhotoContext( ctxPhoto );
			ns.setMyStatus( owner.getStatus() );
		}
	}

	private String getPhotoSHA1C( String filename, String sha1d ) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update( ("Creator" + loginName).getBytes() );
		md.update( ("Size" + bPhoto.length).getBytes() );
		md.update( "Type3".getBytes() );
		md.update( ("LocationTFR" + filename).getBytes() );
		md.update( "FriendlyAAA=".getBytes() );
		md.update( ("SHA1D" + sha1d).getBytes() );
		return new BASE64(false).encode(md.digest());
	}

	private String getPhotoSHA1D() throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] ret = md.digest( bPhoto );
		return new BASE64(false).encode(ret);	
	}

	private String getIdHash( String id ) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update( id.getBytes() );
		byte[] b = md.digest();

		StringBuffer sb = new StringBuffer();
		for(int i=0; i<4; i++)
		{
			int v = b[i] < 0 ? (int)b[i] + 0x100 : (int)b[i];
			sb.append( Integer.toHexString(v).toUpperCase() );
		}
		sb.append( ".dat" );
		return sb.toString();
	}

	public Image getMyPhoto()
	{
		return this.imgPhoto;
	}

	public byte[] getMyPhotoBytes()
	{
		return this.bPhoto;
	}
*/

	/**
	 * ?˜„?¬ ë¡œê·¸?¸ ?œ ?ƒ?ƒœ?¸ì§? ?™•?¸?•œ?‹¤.
	 */
	public boolean isLoggedIn()
	{
		return this.isLogged;
	}

	/**
	 * ?„œë²„ë¡œë¶??„° ë°œìƒ?˜?Š” ?´ë²¤íŠ¸?‚˜ ë©”ì‹œì§??“¤?„ ì²˜ë¦¬?•  MsnListner
	 * ?¸?„°?˜?´?Š¤ë¥? ?„¤? •?•œ?‹¤. ?›?˜?Š” multi-listenerë¥? ì§??›?•´?•¼?•˜ì§?ë§?,
	 * ?˜„?¬?Š” ?‹¨?¼ Listener êµ¬ì¡°ë¥? ?‚¬?š©?•œ?‹¤. ê·¸ëŸ¬ë¯?ë¡?
	 * ë°˜ë“œ?‹œ ë¦¬ìŠ¤?„ˆë¥? ?„¤? •?•´?•¼?•œ?‹¤.
	 */
	public synchronized void addMsnListener( MsnListener l )
	{
		if( !listeners.contains(l) )
			listeners.add(l);
	}

	/**
	 * ?˜„?¬ ?“±ë¡ëœ MSNListener?˜ ?ˆ˜ë¥? ë°˜í™˜?•´ì¤??‹¤.
	 */
	public int getListenerCount()
	{
		return listeners.size();
	}

	public void fireListAdd( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireListAdd" );
		base.listAdd( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).listAdd(friend);

	}

	public void fireInstantMessageEvent( SwitchboardSession ss, MsnFriend friend,
		MimeMessage mime )
	{
		fireInstantMessageEventImpl( ss, friend, mime );
	}

	protected void fireInstantMessageEventImpl( SwitchboardSession ss, MsnFriend friend, MimeMessage mime )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireInstantMessageEvent" );
		base.instantMessageReceived(ss, friend, mime);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).instantMessageReceived(ss, friend, mime);
	}

	protected void fireJoinSessionEventImpl( SwitchboardSession ss, MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireJoinSessionEvent" );
		base.whoJoinSession(ss, friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoJoinSession(ss, friend);
	}

	public void fireJoinSessionEvent( SwitchboardSession ss, MsnFriend friend )
	{
		fireJoinSessionEventImpl( ss, friend );
	}

	protected void fireListOnlineEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireListOnlineEvent" );
		base.listOnline(friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).listOnline(friend);
	}

	public void fireListOnlineEvent( MsnFriend friend )
	{
		fireListOnlineEventImpl( friend );	
	}

	protected void fireLoginCompleteEventImpl( MsnFriend own )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireLoginCompleteEvent" );
		base.loginComplete(own);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).loginComplete(own);
	}

	public void fireLoginCompleteEvent( MsnFriend own )
	{
		fireLoginCompleteEventImpl( own );
	}

	protected void firePartSessionEventImpl( SwitchboardSession ss, MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: firePartSessionEvent" );
		base.whoPartSession(ss, friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoPartSession(ss, friend);
	}

	public void firePartSessionEvent( SwitchboardSession ss, MsnFriend friend )
	{
		firePartSessionEventImpl( ss, friend );
	}

	protected void fireProgressTypingEventImpl( SwitchboardSession ss, MsnFriend friend, String typeuser )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireProgressTypingEvent" );
		base.progressTyping(ss, friend, typeuser);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).progressTyping(ss, friend, typeuser);
	}

	public void fireProgressTypingEvent( SwitchboardSession ss, MsnFriend friend, String typeuser )
	{
		fireProgressTypingEventImpl( ss, friend, typeuser );
	}

	protected void fireSwitchboardSessionStartedEventImpl( SwitchboardSession ss )
	{
		if( Debug.printFireEvent )
		{
			System.out.println( "* Event: fireSwitchboardSessionStartedEvent" );
		}	

		base.switchboardSessionStarted(ss);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).switchboardSessionStarted(ss);
	}

	public void fireSwitchboardSessionStartedEvent( SwitchboardSession ss )
	{
		fireSwitchboardSessionStartedEventImpl( ss );
	}

	protected void fireSwitchboardSessionEndedEventImpl( SwitchboardSession ss )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireSwitchboardSessionEndedEvent" );
		base.switchboardSessionEnded(ss);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).switchboardSessionEnded(ss);
	}

	public void fireSwitchboardSessionEndedEvent( SwitchboardSession ss )
	{
		fireSwitchboardSessionEndedEventImpl( ss );
	}

	protected void fireSwitchboardSessionAbandonEventImpl( SwitchboardSession ss, String targetName )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireSwitchboardSessionAbandonEvent" );
	    base.switchboardSessionAbandon(ss, targetName);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).switchboardSessionAbandon(ss, targetName);
	}

	public void fireSwitchboardSessionAbandonEvent( SwitchboardSession ss, String targetName )
	{
		fireSwitchboardSessionAbandonEventImpl( ss, targetName );
	}

	protected void fireUserOnlineEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireUserOnlineEvent" );
		base.userOnline(friend);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).userOnline(friend);
	}

	public void fireUserOnlineEvent( MsnFriend friend )
	{
		fireUserOnlineEventImpl( friend );
	}

	protected void fireUserOfflineEventImpl( String loginName )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireUserOfflineEvent" );
		base.userOffline(loginName);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).userOffline(loginName);
	}

	public void fireUserOfflineEvent( String loginName )
	{
		fireUserOfflineEventImpl( loginName );
	}

	protected void fireFilePostedEventImpl( SwitchboardSession ss, int cookie, String filename, int filesize )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFilePostedEvent" );
		base.filePosted(ss, cookie, filename, filesize);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).filePosted(ss, cookie, filename, filesize);
	}

	public void fireFilePostedEvent( SwitchboardSession ss, int cookie, String filename, int filesize )
	{
		fireFilePostedEventImpl( ss, cookie, filename, filesize );
	}

	protected void fireFileSendAcceptedEventImpl( SwitchboardSession ss, int cookie )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendAcceptedEvent" );
		base.fileSendAccepted(ss, cookie);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendAccepted(ss, cookie);
	}

	public void fireFileSendAcceptedEvent( SwitchboardSession ss, int cookie )
	{
		fireFileSendAcceptedEventImpl( ss, cookie );
	}

	protected void fireFileSendRejectedEventImpl( SwitchboardSession ss, int cookie, String reason )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendRejectedEvent" );
		base.fileSendRejected(ss, cookie, reason);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendRejected(ss, cookie, reason);
	}

	public void fireFileSendRejectedEvent( SwitchboardSession ss, int cookie, String reason )
	{
		fireFileSendRejectedEventImpl( ss, cookie, reason );
	}

	protected void fireFileSendStartedEventImpl( VolatileTransferServer server )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendStartedEvent" );
		base.fileSendStarted(server);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendStarted(server);
	}

	public void fireFileSendStartedEvent( VolatileTransferServer server )
	{
		fireFileSendStartedEventImpl( server );
	}

	protected void fireFileSendEndedEventImpl( VolatileTransferServer server )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendEndedEvent" );
		base.fileSendEnded(server);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendEnded(server);
	}

	public void fireFileSendEndedEvent( VolatileTransferServer server )
	{
		fireFileSendEndedEventImpl( server );
	}

	protected void fireFileReceiveStartedEventImpl( VolatileDownloader down )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileReceiveStartedEvent" );
		base.fileReceiveStarted(down);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileReceiveStarted(down);
	}

	public void fireFileReceiveStartedEvent( VolatileDownloader down )
	{
		fireFileReceiveStartedEventImpl( down );
	}

	protected void fireFileSendErrorEventImpl( VolatileTransferServer server, Throwable e )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileSendErrorEvent" );
		base.fileSendError(server, e);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileSendError(server, e);
	}

	public void fireFileSendErrorEvent( VolatileTransferServer server, Throwable e )
	{
		fireFileSendErrorEventImpl( server, e );
	}

	protected void fireFileReceiveErrorEventImpl( VolatileDownloader down, Throwable e )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireFileReceiveErrorEvent" );
		base.fileReceiveError(down, e);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).fileReceiveError(down, e);
	}

	public void fireFileReceiveErrorEvent( VolatileDownloader down, Throwable e )
	{
		fireFileReceiveErrorEventImpl( down, e );
	}

	protected void fireWhoAddedMeEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireWhoAddedMeEvent" );
		base.whoAddedMe( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoAddedMe( friend );
	}

	public void fireWhoAddedMeEvent( MsnFriend friend )
	{
		fireWhoAddedMeEventImpl( friend );
	}

	protected void fireWhoRemovedMeEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireWhoRemovedMeEvent" );
		base.whoRemovedMe( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).whoRemovedMe( friend );
	}

	public void fireWhoRemovedMeEvent( MsnFriend friend )
	{
		fireWhoRemovedMeEventImpl( friend );
	}

	protected void fireBuddyListModifiedEventImpl()
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireBuddyListModifiedEvent" );
		base.buddyListModified();
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).buddyListModified();
	}

	public void fireBuddyListModifiedEvent()
	{
		fireBuddyListModifiedEventImpl();
	}

	protected void fireAddFailedEventImpl( int errcode )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireAddFailedEvent" );
		base.addFailed( errcode );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).addFailed( errcode );
	}

	public void fireAddFailedEvent( int errcode )
	{
		fireAddFailedEventImpl( errcode );
	}

	protected void fireLoginErrorEventImpl( String header )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireLoginErrorEvent" );
		base.loginError(header);
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).loginError(header);
	}

	public void fireLoginErrorEvent( String header )
	{
		fireLoginErrorEventImpl( header );
	}

	protected void fireRenameNotifyEventImpl( MsnFriend friend )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireRenameNotifyEvent" );
		base.renameNotify( friend );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).renameNotify(friend);
	}

	public void fireRenameNotifyEvent( MsnFriend friend )
	{
		fireRenameNotifyEventImpl( friend );
	}

	protected void fireAllListUpdatedEventImpl()
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireAllListUpdatedEvent" );
		base.allListUpdated();
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).allListUpdated();
	}

	public void fireAllListUpdatedEvent()
	{
		fireAllListUpdatedEventImpl();
	}

	protected void fireLogoutNotifyEventImpl()
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireLogoutNotifyEvent" );

	    base.logoutNotify();
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).logoutNotify();
	}

	public void fireLogoutNotifyEvent()
	{
		fireLogoutNotifyEventImpl();
	}

	protected void fireNotifyUnreadMailImpl( Properties mime, int unread )
	{
		if( Debug.printFireEvent )
			System.out.println( "* Event: fireNotifyUnreadMail" );
		base.notifyUnreadMail( mime, unread );
		for(int i=listeners.size()-1; i>=0; i--)
			((MsnListener)listeners.get(i)).notifyUnreadMail( mime, unread );
	}

	public void fireNotifyUnreadMail( Properties mime, int unread )
	{
		fireNotifyUnreadMailImpl( mime, unread );
	}

	/**
	 * ?•´?‹¹ ?´ë²¤íŠ¸ë¦¬ìŠ¤?„ˆë¥? ?•´? œ?•œ?‹¤.
	 */
	public synchronized void removeMsnListener( MsnListener l )
	{
		listeners.remove(l);
	}

	/**
	 * ?„¤? •?œ ë¡œê·¸?¸ ?´ë¦?(LoginName)?„ ë°˜í™˜?•œ?‹¤.
	 */
	public String getLoginName()
	{
		return this.loginName;
	}

	/**
	 * ?‚¬?š©??˜ ë¹„ë??ë²ˆí˜¸ë¥? ë°˜í™˜?•œ?‹¤.
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * ?ê¸? ??‹ ?˜ MsnFriend ?¸?Š¤?„´?Š¤ë¥? ë°˜í™˜?•œ?‹¤.
	 * ë§Œì•½ ë¡œê·¸?¸?•˜ì§? ?•Š?•˜?‹¤ë©?, null?„ ë°˜í™˜?•  ê²ƒì´?‹¤.
	 */
	public MsnFriend getOwner()
	{
		return this.owner;
	}

	/**
	 * DS ?— ? ‘?†?•œ?‹¤.
	 * @deprecated ?ƒˆë¡œìš´ MSN?—?„œ?Š” DSë¥? ?‚¬?š©?•˜ì§? ?•Š?œ¼ë¯?ë¡? ?‚¬?š©?„ ê¸ˆí•œ?‹¤.
	 */
	private void dispatch()
	{
		throw new UnsupportedOperationException("DispatchServer not allowed");
	}

	/*
	 * ?‹¤? œë¡? NS ?— ? ‘?†?•œ?‹¤.
	 */
	protected void loginImpl()
	{
		this.ns = new NotificationProcessor( this, ServerInfo.getDefaultServerInfo(), 1 );
		initLogon();
		this.ns.start();
	}

	/**
	 * ì£¼ì–´ì§? ?´ë¦„ê³¼ ë¹„ë??ë²ˆí˜¸ë¡? ë¡œê·¸?¸?„ ?‹œ?‘?•œ?‹¤.
	 * ?´?Ÿ´ ê²½ìš° ?ƒ?„±??—?„œ ë°›ì?? ?´ë¦„ê³¼ ë¹„ë??ë²ˆí˜¸?Š” ë¬´ì‹œ?œ?‹¤.
	 */
	public void login( String username, String password )
	{
		this.loginName = username;
		this.password = password;

		loginImpl();
	}

	/**
	 * ë¡œê·¸?¸?„ ?‹œ?‘?•œ?‹¤.
	 */
	public void login()
	{
		if( loginName==null || password==null )
			throw new IllegalArgumentException( "Login name and password must not be null" );
		login( this.loginName, this.password );
	}

	/**
	 * ?—´? ¤?ˆ?˜ ëª¨ë“  Switchboard sessionê³¼ì˜ ?—°ê²°ì„ ì¢…ë£Œ?•˜ê³?
	 * DS, NS?— Logout?•œ ?›„ ?—°ê²°ì„ ì¢…ë£Œ?•œ?‹¤.
	 */
	public void logout()
	{
		Enumeration e = sessionMap.elements();
		while( e.hasMoreElements() )
		{
			SwitchboardSession ss = (SwitchboardSession)e.nextElement();
			ss.interrupt();
			ss.cleanUp();
		}
		sessionMap.clear();
		
		if( ns!=null )
		{
			ns.interrupt();
			try	{
				ns.logout();
			} catch( IOException ex ) {}
			ns = null;
		}
	}

	/**
	 * ??‹ ?˜ ?ƒ?ƒœë¥? ë³?ê²½í•œ?‹¤. ?ƒ?ƒœ ë¬¸ì?—´??? UserStatus ?¸?„°?˜?´?Š¤?—
	 * ? •?˜?˜?–´?ˆ?Š” ë¬¸ì?—´ë§Œì„ ?‚¬?š©?•˜?—¬?•¼ë§? ?•œ?‹¤.
	 * ë¡œê·¸?¸?´ ??‚œ ì§í›„?—?Š” Defaultë¡? ?˜¨?¼?¸ ?ƒ?ƒœê°? ?˜?–´?ˆ?„ ê²ƒì´?‹¤.
	 */
	public void setMyStatus( String status ) throws IOException
	{
		this.ns.setMyStatus( status );
	}

	/**
	 * ?˜„?¬ ?ê¸? ??‹ ?˜ ?ƒ?ƒœì½”ë“œê°’ì„ ?–»?–´?˜¨?‹¤.
	 */
	public String getMyStatus()
	{
		if( ns==null )
			return UserStatus.OFFLINE;
		return this.ns.getMyStatus();
	}

	public void setMyFriendlyName( String newName ) throws IOException
	{
		this.ns.setMyFriendlyName( newName );
	}

	/**
	 * ????™”?š”ì²??„ ê±´ë‹¤. ë¹„ë™ê¸°ì ?œ¼ë¡? ì²˜ë¦¬?˜ê¸? ?•Œë¬¸ì—,
	 * ?´ ë©”ì†Œ?“œ?˜ ?‘?—…?´ ??‚¬?‹¤ê³? ?•´?„œ ?—°ê²°ì´ ?´ë£¨ì–´ì§??Š” ê²ƒì?? ?•„?‹ˆ?‹¤.
	 * ?´ ë©”ì†Œ?“œ?Š” ê³§ë°”ë¡? return ?œ?‹¤.
	 * <p>
	 * ë³´í†µ ?—°ê²°ì´ ?´ë£¨ì–´ì§??Š”?°?Š” 2-3ì´? ? •?„?˜ ?‹œê°„ì´ ê±¸ë¦°?‹¤.
	 */
	public void doCall( String loginName ) throws IOException
	{
		ns.doCallFriend( loginName );
	}

	public void addFriend( String loginName ) throws IOException
	{
		ns.requestAdd( loginName );
	}

	public void addFriendAsList( String loginName, String listKind ) 
		throws IOException, IllegalArgumentException
	{
		ns.requestAddAsList( loginName, listKind );
	}

	public void blockFriend( String loginName ) throws IOException
	{
		ns.requestBlock( loginName, false );
	}

	public void unBlockFriend( String loginName ) throws IOException
	{
		ns.requestBlock( loginName, true );
	}

	public void removeFriend( String loginName ) throws IOException
	{
		ns.requestRemove( loginName );
	}

	public void removeFriendAsList( String loginName, String listKind ) 
		throws IOException, IllegalArgumentException
	{
		ns.requestRemoveAsList( loginName, listKind );
	}

	public void addGroup( String groupName ) throws IOException
	{
		ns.requestCreateGroup( groupName );
	}

	public void removeGroup( String groupIndex ) throws IOException
	{
	    ns.requestRemoveGroup( groupIndex );
	}

	public void renameGroup( String groupIndex, String newName ) throws IOException
	{
	    ns.requestRenameGroup( groupIndex, newName );
	}

	/**
	 * ì£¼ì–´ì§? ì¹œêµ¬ë¥? ê·¸ë£¹?¸?±?Š¤ old?—?„œ newë¡? ?´?™?•œ?‹¤.
	 */
	public void moveGroupAsFriend( MsnFriend friend, String oldIndex, String newIndex )
		throws IOException
	{
		ns.requestMoveGroup( friend, oldIndex, newIndex );
	}

	/**
	 * doCallê³? ê°™ì?? ?¼?„ ?•˜ì§?ë§?, ?„¸?…˜?´ ?—°ê²°ë ?•Œê¹Œì?? ê³„ì† ê¸°ë‹¤ë¦°ë‹¤?Š” ê²ƒì´
	 * ?‹¤ë¥´ë‹¤.
	 * <p>
	 * Object.wait ë©”ì†Œ?“œë¥? ?‚¬?š©?•˜?—¬ ê¸°ë‹¤ë¦¬ê²Œ ?˜ê³?, ?„¸?…˜ ?—°ê²? ë©”ì‹œì§?ê°? ?˜¬?•Œê¹Œì??
	 * ê³„ì† ???ê¸°í•˜ê²? ?œ?‹¤.
	 */
	public SwitchboardSession doCallWait( String loginName )
		throws IOException, InterruptedException
	{
		return ns.doCallFriendWait( loginName );
	}

	/**
	 * ?•´?‹¹ loginName?´ ?¬?•¨?œ ?„¸?…˜ì¤? ë¬´ì‘?œ„ë¡? ì²«ë²ˆì§? ?„¸?…˜?„ ì°¾ì•„ ë°˜í™˜?•œ?‹¤.
	 * ë§Œì•½ ê·¸ëŸ¬?•œ ?„¸?…˜?´ ì¡´ì¬?•˜ì§? ?•Š?Š”?‹¤ë©?, null?„ ë°˜í™˜?•œ?‹¤.
	 *
	 * @return  loginName?´ ?¬?•¨?œ ?„¸?…˜?´ ?—†?‹¤ë©? null?„ ë°˜í™˜.
	 */
	public SwitchboardSession findSwitchboardSession( String loginName )
	{
		Enumeration e = sessionMap.elements();
		while( e.hasMoreElements() )
		{
			SwitchboardSession ss = (SwitchboardSession)e.nextElement();
			if( ss.isInFriend(loginName) )
				return ss;
		}
		return null;
	}

	/**
	 * ?•´?‹¹ loginNameê³? 1:1ë¡? ?—°ê²°ëœ ?„¸?…˜?„ ì°¾ì•„ì¤??‹¤.
	 * ë§Œì•½ ?„¸?…˜?´ ì¡´ì¬?•˜ì§? ?•Š?Š”?‹¤ë©?, null?„ ë°˜í™˜?•œ?‹¤.
	 */
	public SwitchboardSession findSwitchboardSessionAt( String loginName )
	{
		Enumeration e = sessionMap.elements();
		while( e.hasMoreElements() )
		{
			SwitchboardSession ss = (SwitchboardSession)e.nextElement();
			if( ss.getFriendCount()==1 && ss.isInFriend(loginName) )
				return ss;
		}
		return null;
	}

	/**
	 * ì£¼ì–´ì§? loginName?„ ê°?ì§? ?‚¬?š©??—ê²? MIME ë©”ì‹œì§?ë¥? ? „?‹¬?•œ?‹¤.
	 * ë§Œì•½ loginName?„ ê°?ì§? ?‚¬?š©?????˜ ?—´ë¦? session?´ ì¡´ì¬?•˜ì§? ?•Š?Š”?‹¤ë©?,
	 * ì¦‰ì‹œ falseë¥? ë°˜í™˜?•  ê²ƒì´?‹¤.
	 *
	 * @return true - ?„±ê³µì ?œ¼ë¡? ? „?†¡?•˜????„?•Œ,
	 *         false - ë³´ë‚´ê¸°ê?? ?‹¤?Œ¨?–ˆ?„?•Œ.
	 */
	public boolean sendMessage( String loginName, MimeMessage msg ) throws IOException
	{
		SwitchboardSession ss = findSwitchboardSession(loginName);
		if( ss!=null )
		{
			ss.sendMessage( msg );
			return true;
		}
		return false;
	}

	/**
	 * ì£¼ì–´ì§? loginName?„ ê°?ì§? ?‚¬?š©??—ê²? MIME ë©”ì‹œì§?ë¥? ? „?‹¬?•œ?‹¤.
	 * ????‹  loginName?´ ?—¬?Ÿ¬ê°œì¼ ê²½ìš° sessionId??? ?¼ì¹˜í•˜?Š” ?„¸?…˜?œ¼ë¡œë§Œ ? „?†¡?•œ?‹¤.
	 * ë§Œì•½ ?¼ì¹˜í•˜?Š” ?„¸?…˜?´ ?—†?‹¤ë©? ë©”ì‹œì§??Š” ? „?‹¬?˜ì§? ?•Š?„ ê²ƒì´ë©?,
	 * sessionIdê°? null?´?¼ë©? ì²«ë²ˆì§? ë°œê²¬?˜?Š” ?„?˜?˜ ?„¸?…˜?— ? „?‹¬?  ê²ƒì´?‹¤.
	 * <p>
	 * ë¬¼ë¡  sessionIdê°? null?´ê³? loginName?„ ?¬?•¨?•˜?Š” ?„¸?…˜?´ ì¡´ì¬?•˜ì§? ?•Š?Š”?‹¤ë©?
	 * ? „?†¡?˜ì§? ?•Šê³? falseë¥? ë°˜í™˜?•œ?‹¤.
	 *
	 * @return true - ?„±ê³µì ?œ¼ë¡? ? „?†¡?•˜????„?•Œ,
	 *         false - ë³´ë‚´ê¸°ê?? ?‹¤?Œ¨?–ˆ?„?•Œ.
	 */
	public boolean sendMessage( String loginName, MimeMessage msg, String sessionId )
		throws IOException
	{
		SwitchboardSession ss = (SwitchboardSession)sessionMap.get(sessionId);
		if( ss==null )
		{
			return sendMessage(loginName, msg);
		}
		ss.sendMessage( msg );
		return true;
	}

	/**
	 * ì£¼ì–´ì§? loginName?„ ê°?ì§? ?‚¬?š©??—ê²? MIME ë©”ì‹œì§?ë¥? ? „?‹¬?•œ?‹¤.
	 * ë§Œì•½ ?¼ì¹˜í•˜?Š” ?„¸?…˜?´ ?—†?‹¤ë©? ë©”ì‹œì§??Š” ? „?‹¬?˜ì§? ?•Š?„ ê²ƒì´ë©?,
	 * session?´ null?´?¼ë©? ? „?†¡?˜ì§? ?•Š?„ ê²ƒì´?‹¤.
	 *
	 * @return true - ?„±ê³µì ?œ¼ë¡? ? „?†¡?•˜????„?•Œ,
	 *         false - ë³´ë‚´ê¸°ê?? ?‹¤?Œ¨?–ˆ?„?•Œ.
	 */
	public boolean sendMessage( MimeMessage msg, SwitchboardSession session )
		throws IOException
	{
		if( session!=null )
		{
			session.sendMessage( msg );
			return true;
		}
		return false;
	}

	/**
	 * ?•´?‹¹ sessionId ?„¸?…˜?œ¼ë¡? ?ŒŒ?¼?„ ? „?†¡?•˜ê¸? ?œ„?•´ loginName?—ê²?
	 * ?ŒŒ?¼?„ ?†¡?‹ ?š”ì²? ë©”ì‹œì§?ë¥? ë³´ë‚¸?‹¤.
	 *
	 */
	public void sendFileRequest( String loginName, File file, String sessionId )
		throws IOException
	{
		if( sessionId==null )
			throw new IllegalArgumentException( "session id must not be null" );

		sendFileRequest( loginName, file, (SwitchboardSession)sessionMap.get(sessionId) );
	}

	/**
	 * ?•´?‹¹ sessionId ?„¸?…˜?œ¼ë¡? ?ŒŒ?¼?„ ? „?†¡?•˜ê¸? ?œ„?•´ loginName?—ê²?
	 * ?ŒŒ?¼?„ ?†¡?‹ ?š”ì²? ë©”ì‹œì§?ë¥? ë³´ë‚¸?‹¤.
	 *
	 */
	public void sendFileRequest( String loginName, File file, SwitchboardSession session )
		throws IOException
	{
		if( session==null )
			throw new IllegalArgumentException( "session must not be null" );

		FileTransferMessage msg = FileTransferMessage.createInviteMessage(file);
		ToSendFile toSend = new ToSendFile( msg.getProperty("Invitation-Cookie"), loginName, file );
		session.sendFileRequest( toSend, msg );
	}

	public List getOpenedSwitchboardSessions() 
	{
		ArrayList list = new ArrayList();
		list.addAll( sessionMap.values() );
		return list;
	}

	/////////////////////////////////////////////////////////////////////

	private class Listener extends MsnAdapter
	{
		public void renameNotify( MsnFriend own )
		{
			if( own!=null && owner.getLoginName().equals(own.getLoginName()) )
				owner.setFriendlyName( own.getFriendlyName() );
		}
		public void loginComplete( MsnFriend own )
		{
			isLogged = true;
			owner = own;
			/*
			if( ctxPhoto!=null )
				owner.setPhotoContext( ctxPhoto );
			*/
		}
		public void logoutNotify()
		{
			isLogged = false;
		}

		public void listOnline( MsnFriend friend )
		{
			/*
			 * add ë©”ì†Œ?“œ?Š”, ?´ë¯? ì¡´ì¬?•˜?Š” ?‚¬?š©??¼ë©? ê°’ë§Œ ë³?ê²½í•˜?„ë¡? ?˜?–´?ˆ?‹¤.
			 */
			forwardList.add( friend );
		}

		public void userOnline( MsnFriend friend )
		{
			forwardList.add( friend );
		}

		public void userOffline( String loginName )
		{
			forwardList.setOffline( loginName );
		}

		public void switchboardSessionStarted( SwitchboardSession ss )
		{
			String sid = ss.getSessionId();
			if( sid==null )
				return;

			sessionMap.put( sid, ss );
			/* ?„¸?…˜?´ ?‹œ?‘?˜?—ˆ?‹¤ê³? ?ƒ???ë°©ì´ ë°˜ë“œ?‹œ ?“¤?–´?˜¨ê²ƒì?? ? ˆ??? ?•„?‹ˆ?‹¤. */
		}

		public void switchboardSessionEnded( SwitchboardSession ss )
		{
			String sid = ss.getSessionId();
			sessionMap.remove( sid );
		}

		public void whoAddedMe( MsnFriend friend )
		{
			System.out.println( friend + " Add me." );
		}

		public void whoRemovedMe( MsnFriend friend )
		{
			System.out.println( friend.getLoginName() + " remove me." );
		}
	}
}
