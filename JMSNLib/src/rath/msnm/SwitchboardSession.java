/*
 * @(#)SwitchboardSession.java
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
 *    $Id: SwitchboardSession.java,v 1.22 2005/05/15 17:16:17 xrath Exp $
 */
package rath.msnm;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import rath.msnm.entity.Callback;
import rath.msnm.entity.MsnFriend;
import rath.msnm.entity.ServerInfo;
import rath.msnm.ftp.FileMessageProcessor;
import rath.msnm.ftp.ToSendFile;
import rath.msnm.msg.FileTransferMessage;
import rath.msnm.msg.IncomingMessage;
import rath.msnm.msg.MimeMessage;
import rath.msnm.msg.OutgoingMessage;
/**
 * μΉκ΅¬?€κ³? ???? ?? ?Έ?€?΄?Έ λ©μμ§?λ₯? μ£Όκ³  λ°μ? Channelλ‘? ?¬?©??
 * Session?΄?€. ?΄ ?Έ???? ?¬?©?? ????κ°? ??λ©? μ’λ£??€.
 * <p>
 *
 * @author Jang-Ho Hwang, rath@linuxkorea.co.kr
 * @version $Id: SwitchboardSession.java,v 1.22 2005/05/15 17:16:17 xrath Exp $
 */
public class SwitchboardSession extends AbstractProcessor
{
	private static int timeout = 0;
	private FileMessageProcessor file = null;

	private String sessionId = null;
	private String cookie = null;
	private HashMap friendMap = new HashMap();
	private MsnFriend lastFriend = null;

	private String targetLoginName = null;

	private HashMap slpMessageMap = new HashMap(); 
	private HashMap slpDataMap = new HashMap(); // Key - BaseID, Value=...

	private Object userObject = null;
	private NotificationProcessor NPProcessor;

	public SwitchboardSession( NotificationProcessor NPProcessor, MSNMessenger msn, ServerInfo info, String sessionId )
	{
		super( msn, info, 1 );
		this.NPProcessor = NPProcessor;

		setServerName( "SS" );
		setAutoOutSend( true );

		this.sessionId = sessionId;
		this.file = new FileMessageProcessor(this);
	}

	public void setUserObject( Object o )
	{
		this.userObject = o;
	}

	public Object getUserObject()
	{
		return this.userObject;
	}

	/**
	 * ????λ°©κ³Ό? ?Έ??΄ ?΄λ¦? μ§? ? ?ΈμΆλ? λ©μ??΄?€.
	 */
	protected void sessionOpened()
	{
		this.NPProcessor.addSwitchboardSession(this);
	}

	/**
	 * ????λ₯? κ±°λ ?¬? ??₯?? ?€? λ‘? ????λ°©μ΄ Session? Join?κΈ?
	 * ? ? ?κ΅¬μ?? ?°κ²°λ κ²μΈμ§?λ₯? κ²°μ ?΄?? λ©μ??΄?€.
	 */
	public void setTarget( String loginName )
	{
		this.targetLoginName = loginName;
	}

	/**
	 * ?κ΅¬μ??? ?°κ²°μΈμ§? μ‘°ν??€.
	 */
	public String getTarget()
	{
		return this.targetLoginName;
	}

	/**
	 * ??¬ ?Έ? idλ₯? λ¬Έμ?΄ ??λ‘? λ°ν?΄μ€??€.
	 */
	public String getSessionId()
	{
		return this.sessionId;
	}

	/**
	 * ??¬ ?Έ?? idλ₯? λ¬Έμ?΄ ??λ‘? ?€? ??€.
	 */
	public void setSessionId( String sessionId )
	{
		this.sessionId = sessionId;
	}

	public String getCookie()
	{
		return this.cookie;
	}

	public void setCookie( String cookie )
	{
		this.cookie = cookie;
	}

	protected void makeConnection() throws IOException
	{
		super.makeConnection();
		setTimeout( timeout );
	}

	/**
	 * ?΄ Channel?? ?Όλ§κ° ?λ¬΄λ° λ©μμ§?? ?€κ°?μ§? ??? κ²½μ°
	 * ?°κ²°μ μ’λ£?κ²? ?  ? ???°, ?΄κ³³μ ? ?©?? Timeout?Όλ‘? millisecond?¨?λ‘? ?€? ??€.
	 *
	 * @param  timeout  millisecond?¨?? SO_TIMEOUT
	 */
	public void setTimeout( int timeout )
	{
		SwitchboardSession.timeout = timeout;
		if( socket!=null )
		{
			try
			{
				socket.setSoTimeout( timeout );
			}
			catch( IOException e ) {
				System.err.println( "can't assign SO_TIMEOUT value" );
			}
		}
	}

	/**
	 * ?΄ Channel?? ?Όλ§κ° ?λ¬΄λ° λ©μμ§?? ?€κ°?μ§? ??? κ²½μ°
	 * ?°κ²°μ μ’λ£?κ²? ?  ? ???°, ?΄κ³³μ ? ?©?? Timeout?Όλ‘? millisecond?¨?λ‘? ?»?΄?¨?€.
	 * <p>
	 * defaultλ‘? 180000(3λΆ?)?Όλ‘? ?€? ??΄??€.
	 */
	public int getTimeout()
	{
		return SwitchboardSession.timeout;
	}

	public void init() throws IOException
	{
		Callback cb = Callback.getInstance("processRosterInfo", this.getClass());
		cb.setInfinite();

		OutgoingMessage out = new OutgoingMessage("ANS");
		markTransactionId( out );
		out.add( msn.getLoginName() );
		out.add( cookie );
		out.add( sessionId );
		out.setBackProcess( cb );

		sendMessage( out );
	}

	/**
	 * ?Ή?  ?¬?©?λ₯? ?΄ ?Έ?? μΆκ????€.
	 */
	protected void addMsnFriend( MsnFriend friend )
	{
		friendMap.put( friend.getLoginName(), friend );
		this.lastFriend = friend;
	}

	/**
	 * κ°??₯ μ΅κ·Ό? ?΄ ?Έ?? ?€?΄?¨ μΉκ΅¬λ₯? ?»?΄?¨?€.
	 */
	public MsnFriend getMsnFriend()
	{
		return this.lastFriend;
	}

	/**
	 * μ£Όμ΄μ§? loginName? κ°?μ§? ?¬?©?κ°? ?΄ ?Έ?? λ¬Όλ €??μ§? ??μ§?
	 * ??Έ??€.
	 */
	public boolean isInFriend( String loginName )
	{
		return friendMap.containsKey(loginName);
	}

	/**
	 * ?Ή?  loginName? κ°?μ§? ?¬?©?λ₯? ?΄ ?Έ??? ? κ±°ν?€.
	 */
	protected MsnFriend removeMsnFriend( String loginName )
	{
		return (MsnFriend)friendMap.remove( loginName );
	}

	public Collection getMsnFriends()
	{
		return friendMap.values();
	}

	/**
	 * ??¬ ?΄ ?Έ?? ?°κ²°λ?΄?? μΉκ΅¬? ?λ₯? ?»?΄?¨?€.
	 * ?κΈ? ?? ??? ?΄ ?«??? ? ?Έ??€.
	 */
	public int getFriendCount()
	{
		return friendMap.size();
	}

	/**
	 *
	 */
	protected void processMimeMessage( IncomingMessage msg ) throws Exception
	{
		int off = 0;
		int len = msg.getInt(2);
		int readlen = 0;
		byte[] b = new byte[ len ];

		while( off < len )
		{
			readlen = in.read(b, off, len-off);
			if( readlen==-1 )
				break;
			off += readlen;
		}

		MimeMessage mime = MimeMessage.parse( b );

		if( Debug.printMime )
		{
			System.out.println(new String(b, "UTF-8"));
		}

		int kind = mime.getKind();
		switch( kind )
		{
		case MimeMessage.KIND_TYPING_USER:
			processTypingUser( msg, mime );
			break;
		case MimeMessage.KIND_MESSAGE:
			processInstantMessage( msg, mime );
			break;
		case MimeMessage.KIND_FILE_TRANSFER:
			file.processMessage( msg, mime );
			break;
		case MimeMessage.KIND_UNKNOWN:
			break;
		}
	}

	protected void processWhoJoined( IncomingMessage msg ) throws Exception
	{
		String loginName = msg.get(0);
		String friendlyName = msg.get(1);

		MsnFriend friend = msn.getBuddyGroup().getAllowList().get( loginName );
		if( friend==null )
		{
			friend = new MsnFriend(loginName, friendlyName);
		}

		friend.setFriendlyName( friendlyName );
		addMsnFriend( friend );
		msn.fireJoinSessionEvent( this, friend );
	}

	/**
	 * Switchboard Serverλ‘λ???° notify?? λ©μμ§??€? μ²λ¦¬??€.
	 */
	public void processNotifyMessage( IncomingMessage msg ) throws Exception
	{
		String header = msg.getHeader();
		if( header.equals("MSG") )
			processMimeMessage(msg);
		else
		if( header.equals("JOI") )
			processWhoJoined(msg);
		else
		if( header.equals("BYE") )
		{
			String partLoginName = msg.get(0);
			MsnFriend parter = removeMsnFriend( partLoginName );

			if( parter!=null )
				msn.firePartSessionEvent( this, parter );
			if( friendMap.size()==0 )
			{
				isLive = false;
			}
		}
	}

	/**
	 * ?€? ?κ°? μ’λ£?κ³? ?€?Έλ¦Όμ ?«κΈ? μ§μ ? ?ΈμΆλ?€.
	 */
	public void cleanUp()
	{
		try
		{
			this.NPProcessor.removeSwitchboardSession(this);
			close();
		}
		catch( IOException e ) {}

		

		friendMap.clear();
		if( sessionId!=null )
			msn.fireSwitchboardSessionEndedEvent( this );
	}

	/**
	 * ?΄ ?Έ?? μ’λ£??€.
	 */
	public void close() throws IOException
	{
		isLive = false;
		this.NPProcessor.removeSwitchboardSession(this);

		OutgoingMessage out = new OutgoingMessage("OUT");
		sendMessage( out );
	//	interrupt();
	}

	public void processRosterInfo( IncomingMessage msg ) throws IOException
	{
		String header = msg.getHeader();
		if( header.equals("IRO") )
		{
			String destLoginName = msg.get(2);
			String destFriendlyName = msg.get(3);

			MsnFriend friend = msn.getBuddyGroup().getAllowList().get( destLoginName );
			// TODO: by sediah
			if (friend != null) {
				friend.setFriendlyName( destFriendlyName );
				addMsnFriend( friend );
			}
			else
			{
				System.err.println( "* Not found in allow list: " + destLoginName );
				addMsnFriend( new MsnFriend(destLoginName, destFriendlyName) );
			}
		}
		else
		if( header.equals("ANS") )
		{
			removeInfiniteTransactionId( msg.getTransactionId() );
			String returnCode = msg.get(0);
			if( returnCode.equals("OK") )
			{
				msn.fireSwitchboardSessionStartedEvent( this );
				sessionOpened();
			}
		}
	}

	/**
	 * ?κ΅°κ?? ?? ? ?₯?΄ ?Έ?€?΄?Έ λ©μμ§?λ₯? ? λ¦¬κΈ° ??΄ ?€λ³΄λλ₯?
	 * ?€?₯?€?₯ ??€κΈ°κ³  ??? <b>μ’μ’</b> ? ?Ό?€? λ©μμ§??΄?€.
	 */
	protected void processTypingUser( IncomingMessage msg, MimeMessage mime )
		throws IOException
	{
		// ?΄ λ©μ?? ?΄λ²€νΈ λ¦¬μ€?? ?±λ‘ν΄?Ό??€.
		MsnFriend friend = new MsnFriend( msg.get(0), msg.get(1) );
		msn.fireProgressTypingEvent( this, friend, mime.getProperty("TypingUser") );
	}

	/**
	 * ?Έ?€?΄?Έ λ©μμ§?κ°? ?μ°©ν????? κ·? λ©μμ§?λ₯? μ²λ¦¬?? λ©μ??΄?€.
	 */
	protected void processInstantMessage( IncomingMessage msg, MimeMessage mime )
		throws IOException
	{
		MsnFriend friend = new MsnFriend( msg.get(0) );
		friend.setFriendlyName( msg.get(1) );

		msn.fireInstantMessageEvent( this, friend, mime );
	}

	/**
	 * ?΄ ?Έ?? ?? λͺ¨λ  ?¬??κ²? λ©μμ§?λ₯? λ³΄λΈ?€.
	 */
	public void sendMessage( MimeMessage mime ) throws IOException
	{
		/*
		 * markTransactionIdλ₯? λ°λ? λΆμ¬?Όλ§? ??€.
		 */
		OutgoingMessage out = new OutgoingMessage("MSG");
		markTransactionId( out );
		out.add( "N" );

		sendMimeMessage( out, mime );
	}

	/**
	 * ?΄ ?Έ?? λ¬Όλ¦° ?¬??κ²? ??Ό? ? ?‘?κ² λ€? λ©μμ§?λ₯? λ³΄λΈ?€.
	 */
	public void sendFileRequest( ToSendFile file, FileTransferMessage mime ) throws IOException
	{
		this.file.registerSend( file );
		sendMessage( mime );
	}

	/**
	 * ????λ°©μΌλ‘λ???°? ??Ό ??  ?μ²?? <b>??½</b>??€.
	 *
	 * @param  cookie  ??Ό μ΄μ²­? λ°μ? μΏ ν€κ°?.
	 * @param  toReceive  ?€?΄λ‘λ?  ?΄?©?΄ ????₯?  ??Ό.
	 */
	public void acceptFileReceive( int cookie, java.io.File toReceive ) throws IOException
	{
		this.file.registerReceive( cookie, toReceive );
		sendMessage( FileTransferMessage.createAcceptMessage(cookie) );
	}

	/**
	 * ????λ°©μΌλ‘λ???°? ??Ό ??  ?μ²?? <b>κ±°μ </b>??€.
	 *
	 * @param  cookie  ??Ό μ΄μ²­? λ°μ? μΏ ν€κ°?.
	 */
	public void rejectFileReceive( int cookie ) throws IOException
	{
		sendMessage( FileTransferMessage.createRejectMessage(cookie) );
	}

	/**
	 * ????΄? μ€μ΄?Ό? λ©μμ§?λ₯? ? ?‘??€.
	 */
	public void sendTypingMessage( MimeMessage mime ) throws IOException
	{
		mime.setKind( MimeMessage.KIND_TYPING_USER );
		sendMessage( mime );
	}

	/**
	 * ?Έ?€?΄?€ λ©μμ§?λ₯? ? ?‘??€.
	 */
	public void sendInstantMessage( MimeMessage mime ) throws IOException
	{
		mime.setKind( MimeMessage.KIND_MESSAGE );
		sendMessage( mime );
	}

	/**
	 * ?΄ ?Έ??Όλ‘? μΉκ΅¬λ₯? μ΄λ????€.
	 */
	public void inviteFriend( String loginName ) throws IOException
	{
		OutgoingMessage out = new OutgoingMessage( "CAL" );
		markTransactionId( out );
		out.add( loginName );

		sendMessage( out );
	}

	public void processError( Throwable e )
	{
		if( !(e instanceof IOException) )
			e.printStackTrace();
	}

	/**
	 * @return
	 */
	public MsnFriend getOwner() {
		return msn.getOwner();
	}

	/**
	 * @return
	 */
	public MsnFriend getLastFriend() {
		return lastFriend;
	}
	
	/*
	public boolean requestFileTransfer(String filename) {
		File file = new File(filename);
		long totalSize = file.length();
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return false;
		}
		int index = filename.lastIndexOf('/');
		if (index != -1)
			filename = filename.substring(index + 1);
		return requestFileTransfer(filename, totalSize, is);
	}
	*/
};
