package com.imseam.raptor.chatlet;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.BuddyStatusEnum;
import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IBuddy;
import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IStartActiveWindowCallback;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.InviteBuddyException;
import com.imseam.chatlet.exception.StartActiveWindowException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerConnection;

public class ConnectionContext extends AbstractContext implements IConnection {

	private static Log log = LogFactory.getLog(ConnectionContext.class);
	
	private IMessengerConnection connection = null;
	
//	private ConcurrentHashMap<String, MessengerBuddy> buddyMap = new ConcurrentHashMap<String, MessengerBuddy>();
	
	private ConcurrentHashMap<String, Set<IWindow>> buddyToWindowMap = new ConcurrentHashMap<String, Set<IWindow>>();
	
	private final String uid;
	
	public ConnectionContext(IMessengerConnection connection){
		super(true);
		this.connection = connection;
		assert(this.connection != null);
		uid = UidHelper.constructConnectionUid(connection.getServiceId(), connection.getHostUserId());
		log.debug("A ConnectionContext is created: " + uid);
	}
	
	public String getHostUserId() {
		return connection.getHostUserId();
	}

	public String getConnectionName() {
		return connection.getConnectionName();
	}

	public boolean isConnected(){
		return connection.isConnected();
	}

	public String getServiceId() {
		return connection.getServiceId();
	}
	
	public BuddyStatusEnum getBuddyStatus(String messengerID) {
		return connection.getBuddyStatus(messengerID);
	}
	
	public IApplication getApplication() {

		return connection.getApplication().getApplicationContext();
	}
	
	@Override
	public int getOpenWindowLimit() {
		
		return connection.getWindowLimit();
	}

	@Override
	public Collection<? extends IBuddy> getAllBuddies() {
		return connection.getAllBuddies();
	}

	@Override
	public Collection<? extends IBuddy> getOnlineBuddies() {
		return connection.getOnlineBuddies();
	}
	
	@Override
	public IBuddy getBuddyByUserId(String userId){
		return connection.getBuddyByUserId(userId);
	}
	
	@Override
	public MessengerBuddy getBuddyByBuddyUid(String buddyId){
		return connection.getBuddyByBuddyUid(buddyId);
	}

	@Override
	public void inviteBuddy(String userId) throws InviteBuddyException {
		connection.inviteBuddy(userId);		
	}

	@Override
	public String getUid() {
		return uid;
	}

	@Override
	public UidType getUidType() {
		return UidType.CONNECTION;
	}
	
	public IChatletApplication getChatletApplication(){
		return connection.getApplication();
	}

	@Override
	public Set<IWindow> getBuddyActiveWindowSet(String buddyUid) {
		return this.buddyToWindowMap.get(buddyUid);
	}

	public void addActiveWindow(IWindow window) {
		assert(window != null);
		for(IChannel channel : window.getOnboardChannels()){
			String buddyUid = channel.getBuddy().getUid();
			addBuddyActiveWindow(buddyUid, window);
		}
	}

	public void addBuddyActiveWindow(String buddyUid, IWindow window) {
		assert(window != null);
		Set<IWindow> windowSet = buddyToWindowMap.get(buddyUid);
		if(windowSet == null){
			buddyToWindowMap.putIfAbsent(buddyUid, new CopyOnWriteArraySet<IWindow>());
			windowSet = buddyToWindowMap.get(buddyUid);
		}
		windowSet.add(window);
	}

	public boolean removeActiveWindow(IWindow window) {
		assert(window != null);
		boolean oldWindowExisting = false; 
		for(IChannel channel : window.getOnboardChannels()){
			String buddyUid = channel.getBuddy().getUid();
			oldWindowExisting |= removeBuddyActiveWindow(buddyUid, window);;
		}
		return oldWindowExisting;
	}

	public boolean removeBuddyActiveWindow(String buddyUid, IWindow window) {
		Set<IWindow> windowSet = buddyToWindowMap.get(buddyUid);
		boolean oldWindowExisting = false; 			 
		if(windowSet != null){
			oldWindowExisting = windowSet.remove(window);
		}
		return oldWindowExisting;
	}
	
	@Override
	public void startActiveWindow(String buddyUid, IStartActiveWindowCallback callback) throws StartActiveWindowException{
		connection.startActiveWindow(buddyUid, callback);
	}
}
