package com.imseam.raptor.chatlet;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.ChannelExpiredException;
import com.imseam.chatlet.exception.SessionExpiredException;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.config.ImseamRaptorConfig;
import com.imseam.raptor.core.ChannelTask;
import com.imseam.raptor.threading.RaptorTaskQueue;

public class ChannelContext extends AbstractContext implements IChannel{

	private static Log log = LogFactory.getLog(ChannelContext.class);
	
	private IMessengerWindow window = null;
	
	private MessengerBuddy buddy;
	
	private volatile long lastAccessTime;
	
	private volatile long liveTime = ImseamRaptorConfig.instance().getDefaultChannelTimeOut();
	
	private volatile Boolean destroying = false;
	
	private String uid;
	
	private Locale locale;
	
	public ChannelContext(IMessengerWindow window, MessengerBuddy buddy){
		super(false);
		assert(buddy != null);
		assert(window != null);

		this.buddy = buddy;
		
		this.window = window;
		
		getUserSession().addChannel(this);
		
		
		uid = window.getWindowContext().getUid() + "@@@" + buddy.getUid();
		lastAccessTime = System.currentTimeMillis();
		log.debug("A ChannelContext is created:" + uid);
	}
	
	public UserSession getUserSession(){
		
		return  (UserSession)window.getConnection().getApplication().getSessionManager().findUserSession(buddy.getUid()); 
	}
	
	public MessengerBuddy getBuddy() {
		return buddy;
	}

	public IConnection getConnection() {
		return window.getWindowContext().getConnection();
	}

	public IMeeting getMeeting() {
		return window.getWindowContext().getMeeting();
	}
	
	public boolean invalidate() {
		if (liveTime == -1)
			return false;

		if (System.currentTimeMillis() >= lastAccessTime + liveTime) {
			
			ChannelTask channelTask = ChannelTask.createChannelTimeoutTask(window.getWindowContext(), this);
			
			RaptorTaskQueue.getInstance(window.getWindowContext().getUid()).addTask(channelTask);
			
		}
		return false;
	}
	
	public boolean timeoutConfirm(){
		synchronized(this.destroying){
			if (System.currentTimeMillis() >= lastAccessTime + liveTime){
				this.destroying = Boolean.TRUE;
				return true;
			}
		}
		return false;
	}
	
	
	public void access() throws SessionExpiredException, ChannelExpiredException{
		synchronized(destroying){
			lastAccessTime = System.currentTimeMillis();
			if(destroying){
				throw new ChannelExpiredException("The channel is expired :" + uid);
			}
		}
		this.getUserSession().access();
	}
	
	public void setLiveTime(long liveTime) {
		this.liveTime = liveTime;
		
	}

	public long getLiveTime() {
		return this.liveTime;
	}
	
	@Override
	public String toString(){
		return uid;
	}

	@Override
	public IWindow getWindow() {
		return this.window.getWindowContext();
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public UidType getUidType() {
		return UidType.CHANNEL;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

}
