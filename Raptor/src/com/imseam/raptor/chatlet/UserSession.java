package com.imseam.raptor.chatlet;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.imseam.chatlet.ISession;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.SessionExpiredException;
import com.imseam.raptor.config.ImseamRaptorConfig;

public class UserSession extends AbstractContext implements ISession {

//	private static Log log = LogFactory.getLog(UserSession.class);

	private MessengerBuddy user;
	private volatile long lastAccessTime;
	private volatile long liveTime = ImseamRaptorConfig.instance().getDefaultSessionTimeOut();
	private Set<ChannelContext> channelSet = new CopyOnWriteArraySet<ChannelContext>();
	private volatile Boolean destroying = false;
	private String uid;
	
	
	public UserSession(MessengerBuddy user){
		super(true);
		assert(user != null);
		this.user = user;
		lastAccessTime = System.currentTimeMillis();
		uid = user.getUid() + ":::" + lastAccessTime; 
	}
	
	public MessengerBuddy getMessengerUser() {
		return user;
	}

	public boolean addChannel(ChannelContext channel){
		return channelSet.add(channel);
	}
	
	public boolean removeChannel(ChannelContext channel){
		return channelSet.remove(channel);
	}

	public Set<IWindow> getAvailableWindow() {
		Set<IWindow> windowSet = new HashSet<IWindow>();
		for(ChannelContext channel: channelSet){
			windowSet.add(channel.getWindow());
		}
		return windowSet;
	}


	public boolean checkSessionTimeOut() {
		if(liveTime == -1) return false;
		
		if(this.channelSet.size() != 0){
			for(ChannelContext channel : channelSet){
				channel.invalidate();
			}
			return false;
		}

		synchronized(destroying){
			if(System.currentTimeMillis()>= lastAccessTime +liveTime){
				destroying = Boolean.TRUE;
				return true;
			}
		}

		return false;
	}
	
	public void access() throws SessionExpiredException{
		synchronized(destroying){
			lastAccessTime = System.currentTimeMillis();
			if(destroying){
				throw new SessionExpiredException(String.format("The user(%s:%s) session is expired!", user.getServiceId(), user.getUid()));
			}
		}
	}
	
	public boolean isDestorying(){
		return this.destroying;
	}
	
	public void setLiveTime(long liveTime) {
		this.liveTime = liveTime;
		
	}

	public long getLiveTime() {
		return this.liveTime;
	}

	@Override
	public MessengerBuddy getBuddy() {
		return user;
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public UidType getUidType() {
		return UidType.SESSION;
	}
}
