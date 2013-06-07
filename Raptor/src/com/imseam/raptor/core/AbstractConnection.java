package com.imseam.raptor.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IBuddy;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.config.util.ConfigUtil;
import com.imseam.chatlet.listener.event.BuddyEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.chatlet.listener.event.WindowEvent;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IEventListenerManager;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.IWindowManager;
import com.imseam.raptor.chatlet.ConnectionContext;
import com.imseam.raptor.chatlet.EventTypeEnum;
import com.imseam.raptor.chatlet.MessengerBuddy;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.chatlet.UserSession;

public abstract class AbstractConnection implements IMessengerConnection{

	private static Log log = LogFactory.getLog(AbstractConnection.class);
	
	private IEventListenerManager eventListenerManager = null;
	
//	private StandardUserSessionManager sessionManager = null;
	
	private IWindowManager windowManager = null;
	
	private ConnectionContext connectionContext = null;  
	
	protected IChatletApplication application = null;
	
	protected HashMap<String, String> config = null;
	
	private ConcurrentHashMap<String, MessengerBuddy> buddyMap = new ConcurrentHashMap<String, MessengerBuddy>();
	
	private ConcurrentHashMap<String, MessengerBuddy> onlineBuddyMap = new ConcurrentHashMap<String, MessengerBuddy>();
	
	private String hostUserName;
	private String connectionName;
	private String serviceID;
	private String password;
	
	
	
	protected AbstractConnection(IChatletApplication application, ConnectionConfig config){
		this.application = application;
		this.windowManager = application.getWindowManager();
		this.config = ConfigUtil.convertParams(config.getInitParams());
		eventListenerManager = application.getEventListenerManager();
		this.hostUserName = config.getHostUserID();
		this.connectionName = config.getConnectionName();
		this.serviceID = config.getServiceID();
		this.password = config.getPassword();
		this.connectionContext = new ConnectionContext(this);
		assert(eventListenerManager != null);
		assert(config != null);
		assert(!StringUtil.isNullOrEmptyAfterTrim(connectionName));
		assert(!StringUtil.isNullOrEmptyAfterTrim(serviceID));
	}
	
	
	public ConnectionContext getConnectionContext(){
		return connectionContext;
	}
	

	public void connectionStarted(){
		eventListenerManager.fireEvent(EventTypeEnum.ConnectionStarted, new ConnectionEvent(this, this.getConnectionContext()));
	}
	
	final public void disconnect(){
		disconnecting();
		try{
			eventListenerManager.fireEvent(EventTypeEnum.ConnectionStopped, new ConnectionEvent(this, this.getConnectionContext()));	
		}catch(Exception exp){
			exp.printStackTrace();
		}
		
	}
	
	abstract protected void disconnecting(); 
	
	
	public IChatletApplication getApplication(){
		assert(application != null);
		return application;
	}
	
	private BuddyEvent createBuddyEvent(String userId){
		IBuddy user = buddyMap.get(userId);
		if(user == null){
			log.warn("Cannot find the buddy in buddy list: " + userId);
			return null;
		}
		return new BuddyEvent(this, this.getConnectionContext(), user); 
	}
	
	public void fireBuddyAdded(String userId){
		BuddyEvent event = createBuddyEvent(userId);
		if(event != null){
			eventListenerManager.fireEvent(EventTypeEnum.BuddyAdded, event);
		}
	}
	
	public void fireBuddyRemoved(String userId){
		BuddyEvent event = createBuddyEvent(userId);
		if(event != null){
			eventListenerManager.fireEvent(EventTypeEnum.BuddyRemoved, event);
		}
	}
	
	@Override
	public void fireBuddyStatusChange(String userId) {
		BuddyEvent event = createBuddyEvent(userId);
		if(event != null){
			eventListenerManager.fireEvent(EventTypeEnum.BuddyStatusChange, event);
		}	
	}

	
	public void fireBuddySignIn(String userId){
		BuddyEvent event = createBuddyEvent(userId);
		if(event != null){
			eventListenerManager.fireEvent(EventTypeEnum.BuddySignIn, event);
		}
	}

	public void fireBuddySignOff(String userId){
		BuddyEvent event = createBuddyEvent(userId);
		if(event != null){
			eventListenerManager.fireEvent(EventTypeEnum.BuddySignOff, event);
			this.onlineBuddyMap.remove(userId);
		}
	}
	
	public void fireBuddyStatusChange(String userId, BuddyStatusEnum newStatus) {
		BuddyEvent event = createBuddyEvent(userId);
		
		if(event != null){
			MessengerBuddy buddy = ((MessengerBuddy)event.getBuddy()); 
			buddy.setBuddyStatus(newStatus);
			if(BuddyStatusEnum.online_available.equals(newStatus) ){
				this.onlineBuddyMap.putIfAbsent(userId, buddy);
			}else{
				this.onlineBuddyMap.remove(userId);
			}
			eventListenerManager.fireEvent(EventTypeEnum.BuddyStatusChange, event);
			
		}
	}

	private WindowEvent createWindowEvent(IMessengerWindow window){
		return new WindowEvent(this, this.getConnectionContext(), window.getWindowContext());
	}
	
	public void fireWindowStarted(IMessengerWindow window){
		this.getConnectionContext().addActiveWindow(window.getWindowContext());
		windowManager.onWindowCreated(window);
		eventListenerManager.fireEvent(EventTypeEnum.WindowStarted, createWindowEvent(window));

	}
	
	public void fireWindowStopped(IMessengerWindow window){
		this.getConnectionContext().removeActiveWindow(window.getWindowContext());
		windowManager.onWindowStopped(window);
		eventListenerManager.fireEvent(EventTypeEnum.windowStopped, createWindowEvent(window));
	}
	
	@Override
	public String getHostUserId(){
		return this.hostUserName;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public String getConnectionName(){
		return this.connectionName;
	}
	
	@Override
	public String getServiceId(){
		return this.serviceID;
	}
	
	public Collection<MessengerBuddy> getBuddies() {
		return this.buddyMap.values();
	}
	
	public MessengerBuddy getBuddy(String userId, boolean create){
		MessengerBuddy user = buddyMap.get(userId);
		if((user == null) && create){
			user = addMessengerUser(userId, false);
			log.debug(String.format("Messenger User (%s) is not existing in connection, a new one is created", user)); 
		}
		return user;
	}

	protected MessengerBuddy addMessengerUser(String userId, boolean fireEvent){
		MessengerBuddy user = this.buddyMap.get(userId);
		if(user != null){
			log.warn("Trying to add an existing user!");
			return user;
		}
		user = new MessengerBuddy(userId, getServiceId(), this.getConnectionContext().getUid());
		MessengerBuddy oldUser = buddyMap.putIfAbsent(userId, user);
		
		if((fireEvent) && (oldUser == null)){
			fireBuddyAdded(userId);
		}
		
		return user;
	}
	
	protected MessengerBuddy removeMessengerUser(String userId, boolean fireEvent){
		
		MessengerBuddy old = this.buddyMap.get(userId);
		
		if(old == null){
			log.warn("Trying to remove a NON-existing user!");
			return null;
		}

		if(fireEvent) {
			fireBuddyRemoved(userId); 
		}

		return buddyMap.remove(userId);
	}
	
	@Override
	public Collection<MessengerBuddy> getAllBuddies(){
		return buddyMap.values();
	}
	
	@Override
	public Collection<MessengerBuddy> getOnlineBuddies(){
		return onlineBuddyMap.values();
	}
	
	@Override
	public MessengerBuddy getBuddyByUserId(String userId){
		
		return this.buddyMap.get(userId);
	}

	@Override
	public MessengerBuddy getBuddyByBuddyUid(String buddyUid){
		String userId = UidHelper.parseUseridFromBuddyUid(buddyUid);
		return this.buddyMap.get(userId);
	}
	
	
	public void sessionStopped(UserSession oldSession){
		//do nothing
	}
	
	public int getWindowLimit(){
		return 1;
	}
	
	public String getUid(){
		return this.connectionContext.getUid();
	}
	
}
