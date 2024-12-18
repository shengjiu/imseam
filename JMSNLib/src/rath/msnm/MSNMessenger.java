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
 * MSN λ©μ ? Έ ?λ²μ ? ??κ³? ?΄κ²μ??κ²μ ?κΈ? ??΄
 * Entry pointκ°? ?? ?΄??€?΄?€.
 * login? ?μ²??κ³? loginComplete ?΄λ²€νΈκ°? λ°μ? ?λΆ??°
 * ?΄κ²μ??κ²? λ©μ?λ₯? ?¬?©?  ? ??€.
 * κ·Έλ μ§? ??Όλ©? NS proc λ―Έμ?±?Όλ‘? NullPointerException? λ§λ κ²μ΄?€.
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
	 * MSNMessenger κ°μ²΄λ₯? ??±??€.
	 */
	public MSNMessenger()
	{
		this( null, null );
	}

	/**
	 * μ£Όμ΄μ§? account? λ³΄λ‘ MSNMessenger κ°μ²΄λ₯? ??±??€.
	 *
	 * @param  loginName  ?¬?©?  login ?΄λ¦?. (e.g. windrath@hotmail.com)
	 * @param  password   ?? ? password
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

		/* μ΅κ·Ό ?λ¦¬μΌ?λ²λ?? NS? ?€? ??€ */
		localCopy.loadInformation();
		ns.lastFrom = localCopy.getProperty("SerialFrom", "0");
		ns.lastTo   = localCopy.getProperty("SerialTo", "0");
		ns.lastFN   = localCopy.getProperty("FriendlyName", loginName);

		/* μ΅κ·Ό λ²λ λ¦¬μ€?Έλ₯? κ°?? Έ?¨?€. */
		localCopy.loadBuddies( buddyGroup );
	}

	/**
	 * ?λ‘μ΄ ?λ¦¬μΌ λ²νΈλ₯? ????₯? ?, BuddyList?΄ λ³?κ²½λ??€? ?΄λ²€νΈλ₯? λ°μ‘??€.
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
	 * λ‘κ·Έ?Έ ? ?? μ΄κΈ° ??κ°μ ?€? ??€. κΈ°λ³Έκ°μΌλ‘λ '?¨?Ό?Έ'?΄?€.
	 * ?΄κ³³μ ? ?©?  ? ?? ??κ°λ€??? {@link UserStatus UserStatus} ?Έ?°??΄?€?
	 * ? ?Έ??΄?? ???€? ?¬?©?λ©? ??€.
	 */
	public void setInitialStatus( String code )
	{
		this.initStatus = code;
	}

	/**
	 * ??¬ ?€? ? μ΄κΈ° ??μ½λλ₯? ?»?΄?¨?€.
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
	 * ??¬ λ‘κ·Έ?Έ ? ???Έμ§? ??Έ??€.
	 */
	public boolean isLoggedIn()
	{
		return this.isLogged;
	}

	/**
	 * ?λ²λ‘λΆ??° λ°μ?? ?΄λ²€νΈ? λ©μμ§??€? μ²λ¦¬?  MsnListner
	 * ?Έ?°??΄?€λ₯? ?€? ??€. ??? multi-listenerλ₯? μ§???΄?Ό?μ§?λ§?,
	 * ??¬? ?¨?Ό Listener κ΅¬μ‘°λ₯? ?¬?©??€. κ·Έλ¬λ―?λ‘?
	 * λ°λ? λ¦¬μ€?λ₯? ?€? ?΄?Ό??€.
	 */
	public synchronized void addMsnListener( MsnListener l )
	{
		if( !listeners.contains(l) )
			listeners.add(l);
	}

	/**
	 * ??¬ ?±λ‘λ MSNListener? ?λ₯? λ°ν?΄μ€??€.
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
	 * ?΄?Ή ?΄λ²€νΈλ¦¬μ€?λ₯? ?΄? ??€.
	 */
	public synchronized void removeMsnListener( MsnListener l )
	{
		listeners.remove(l);
	}

	/**
	 * ?€? ? λ‘κ·Έ?Έ ?΄λ¦?(LoginName)? λ°ν??€.
	 */
	public String getLoginName()
	{
		return this.loginName;
	}

	/**
	 * ?¬?©?? λΉλ??λ²νΈλ₯? λ°ν??€.
	 */
	public String getPassword()
	{
		return this.password;
	}

	/**
	 * ?κΈ? ?? ? MsnFriend ?Έ?€?΄?€λ₯? λ°ν??€.
	 * λ§μ½ λ‘κ·Έ?Έ?μ§? ???€λ©?, null? λ°ν?  κ²μ΄?€.
	 */
	public MsnFriend getOwner()
	{
		return this.owner;
	}

	/**
	 * DS ? ? ???€.
	 * @deprecated ?λ‘μ΄ MSN??? DSλ₯? ?¬?©?μ§? ??Όλ―?λ‘? ?¬?©? κΈν?€.
	 */
	private void dispatch()
	{
		throw new UnsupportedOperationException("DispatchServer not allowed");
	}

	/*
	 * ?€? λ‘? NS ? ? ???€.
	 */
	protected void loginImpl()
	{
		this.ns = new NotificationProcessor( this, ServerInfo.getDefaultServerInfo(), 1 );
		initLogon();
		this.ns.start();
	}

	/**
	 * μ£Όμ΄μ§? ?΄λ¦κ³Ό λΉλ??λ²νΈλ‘? λ‘κ·Έ?Έ? ????€.
	 * ?΄?΄ κ²½μ° ??±??? λ°μ?? ?΄λ¦κ³Ό λΉλ??λ²νΈ? λ¬΄μ??€.
	 */
	public void login( String username, String password )
	{
		this.loginName = username;
		this.password = password;

		loginImpl();
	}

	/**
	 * λ‘κ·Έ?Έ? ????€.
	 */
	public void login()
	{
		if( loginName==null || password==null )
			throw new IllegalArgumentException( "Login name and password must not be null" );
		login( this.loginName, this.password );
	}

	/**
	 * ?΄? €?? λͺ¨λ  Switchboard sessionκ³Όμ ?°κ²°μ μ’λ£?κ³?
	 * DS, NS? Logout? ? ?°κ²°μ μ’λ£??€.
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
	 * ?? ? ??λ₯? λ³?κ²½ν?€. ?? λ¬Έμ?΄??? UserStatus ?Έ?°??΄?€?
	 * ? ???΄?? λ¬Έμ?΄λ§μ ?¬?©??¬?Όλ§? ??€.
	 * λ‘κ·Έ?Έ?΄ ?? μ§ν?? Defaultλ‘? ?¨?Ό?Έ ??κ°? ??΄?? κ²μ΄?€.
	 */
	public void setMyStatus( String status ) throws IOException
	{
		this.ns.setMyStatus( status );
	}

	/**
	 * ??¬ ?κΈ? ?? ? ??μ½λκ°μ ?»?΄?¨?€.
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
	 * ?????μ²?? κ±΄λ€. λΉλκΈ°μ ?Όλ‘? μ²λ¦¬?κΈ? ?λ¬Έμ,
	 * ?΄ λ©μ?? ???΄ ??¬?€κ³? ?΄? ?°κ²°μ΄ ?΄λ£¨μ΄μ§?? κ²μ?? ???€.
	 * ?΄ λ©μ?? κ³§λ°λ‘? return ??€.
	 * <p>
	 * λ³΄ν΅ ?°κ²°μ΄ ?΄λ£¨μ΄μ§???°? 2-3μ΄? ? ?? ?κ°μ΄ κ±Έλ¦°?€.
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
	 * μ£Όμ΄μ§? μΉκ΅¬λ₯? κ·Έλ£Ή?Έ?±?€ old?? newλ‘? ?΄???€.
	 */
	public void moveGroupAsFriend( MsnFriend friend, String oldIndex, String newIndex )
		throws IOException
	{
		ns.requestMoveGroup( friend, oldIndex, newIndex );
	}

	/**
	 * doCallκ³? κ°μ?? ?Ό? ?μ§?λ§?, ?Έ??΄ ?°κ²°λ ?κΉμ?? κ³μ κΈ°λ€λ¦°λ€? κ²μ΄
	 * ?€λ₯΄λ€.
	 * <p>
	 * Object.wait λ©μ?λ₯? ?¬?©??¬ κΈ°λ€λ¦¬κ² ?κ³?, ?Έ? ?°κ²? λ©μμ§?κ°? ?¬?κΉμ??
	 * κ³μ ???κΈ°νκ²? ??€.
	 */
	public SwitchboardSession doCallWait( String loginName )
		throws IOException, InterruptedException
	{
		return ns.doCallFriendWait( loginName );
	}

	/**
	 * ?΄?Ή loginName?΄ ?¬?¨? ?Έ?μ€? λ¬΄μ?λ‘? μ²«λ²μ§? ?Έ?? μ°Ύμ λ°ν??€.
	 * λ§μ½ κ·Έλ¬? ?Έ??΄ μ‘΄μ¬?μ§? ???€λ©?, null? λ°ν??€.
	 *
	 * @return  loginName?΄ ?¬?¨? ?Έ??΄ ??€λ©? null? λ°ν.
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
	 * ?΄?Ή loginNameκ³? 1:1λ‘? ?°κ²°λ ?Έ?? μ°Ύμμ€??€.
	 * λ§μ½ ?Έ??΄ μ‘΄μ¬?μ§? ???€λ©?, null? λ°ν??€.
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
	 * μ£Όμ΄μ§? loginName? κ°?μ§? ?¬?©??κ²? MIME λ©μμ§?λ₯? ? ?¬??€.
	 * λ§μ½ loginName? κ°?μ§? ?¬?©????? ?΄λ¦? session?΄ μ‘΄μ¬?μ§? ???€λ©?,
	 * μ¦μ falseλ₯? λ°ν?  κ²μ΄?€.
	 *
	 * @return true - ?±κ³΅μ ?Όλ‘? ? ?‘??????,
	 *         false - λ³΄λ΄κΈ°κ?? ?€?¨???.
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
	 * μ£Όμ΄μ§? loginName? κ°?μ§? ?¬?©??κ²? MIME λ©μμ§?λ₯? ? ?¬??€.
	 * ????  loginName?΄ ?¬?¬κ°μΌ κ²½μ° sessionId??? ?ΌμΉν? ?Έ??Όλ‘λ§ ? ?‘??€.
	 * λ§μ½ ?ΌμΉν? ?Έ??΄ ??€λ©? λ©μμ§?? ? ?¬?μ§? ?? κ²μ΄λ©?,
	 * sessionIdκ°? null?΄?Όλ©? μ²«λ²μ§? λ°κ²¬?? ??? ?Έ?? ? ?¬?  κ²μ΄?€.
	 * <p>
	 * λ¬Όλ‘  sessionIdκ°? null?΄κ³? loginName? ?¬?¨?? ?Έ??΄ μ‘΄μ¬?μ§? ???€λ©?
	 * ? ?‘?μ§? ?κ³? falseλ₯? λ°ν??€.
	 *
	 * @return true - ?±κ³΅μ ?Όλ‘? ? ?‘??????,
	 *         false - λ³΄λ΄κΈ°κ?? ?€?¨???.
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
	 * μ£Όμ΄μ§? loginName? κ°?μ§? ?¬?©??κ²? MIME λ©μμ§?λ₯? ? ?¬??€.
	 * λ§μ½ ?ΌμΉν? ?Έ??΄ ??€λ©? λ©μμ§?? ? ?¬?μ§? ?? κ²μ΄λ©?,
	 * session?΄ null?΄?Όλ©? ? ?‘?μ§? ?? κ²μ΄?€.
	 *
	 * @return true - ?±κ³΅μ ?Όλ‘? ? ?‘??????,
	 *         false - λ³΄λ΄κΈ°κ?? ?€?¨???.
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
	 * ?΄?Ή sessionId ?Έ??Όλ‘? ??Ό? ? ?‘?κΈ? ??΄ loginName?κ²?
	 * ??Ό? ?‘? ?μ²? λ©μμ§?λ₯? λ³΄λΈ?€.
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
	 * ?΄?Ή sessionId ?Έ??Όλ‘? ??Ό? ? ?‘?κΈ? ??΄ loginName?κ²?
	 * ??Ό? ?‘? ?μ²? λ©μμ§?λ₯? λ³΄λΈ?€.
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
			 * add λ©μ??, ?΄λ―? μ‘΄μ¬?? ?¬?©??Όλ©? κ°λ§ λ³?κ²½ν?λ‘? ??΄??€.
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
			/* ?Έ??΄ ?????€κ³? ????λ°©μ΄ λ°λ? ?€?΄?¨κ²μ?? ? ??? ???€. */
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
