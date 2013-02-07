package com.imseam.raptor.chatlet;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IChannel;
import com.imseam.chatlet.IChatletMessage;
import com.imseam.chatlet.IConnection;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IMeeting;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.exception.NoMeetingException;
import com.imseam.chatlet.exception.WindowInOtherMeetingException;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.cluster.IClusterInvocation;
import com.imseam.raptor.cluster.IClusterInvocationDistributor;
import com.imseam.raptor.cluster.invocation.BuddyAddedToMeetingInvocation;
import com.imseam.raptor.cluster.invocation.InMeetingEventInvocationWrapper;
import com.imseam.raptor.cluster.invocation.MeetingStoppedInvocation;
import com.imseam.raptor.cluster.invocation.OtherWindowAddedToMeetingInvocation;
import com.imseam.raptor.cluster.invocation.OtherWindowLeftMeetingInvocation;
import com.imseam.raptor.cluster.invocation.WindowAddedToMeetingInvocation;
import com.imseam.raptor.cluster.invocation.WindowRemovedFromMeetingInvocation;

public class WindowContext extends AbstractContext 
			implements IWindow {
	private static Log log = LogFactory.getLog(WindowContext.class);
	
	private Map<String, ChannelContext> channelMap = new HashMap<String, ChannelContext>();
	
	private ChannelContext defaultChannel = null;
	
	private IMessengerWindow messengerWindow = null;
	
	private static volatile int nextNumber = 0;
	
	private String windowUid = UidHelper.constructServerWiseUniqueId("window-" + (nextNumber++) + "-"); //UUID.randomUUID().toString());
	
	
	private IClusterInvocationDistributor requestDistributor;
	
	private IChatletApplication application;
	
	private IMeeting meeting = null;
	
	private final ReentrantLock meetingLock = new ReentrantLock();

	public WindowContext(IMessengerWindow window){
		super(true);
		this.messengerWindow = window;
		assert(window != null);
		application = window.getConnection().getApplication();
		requestDistributor = application.getClusterInvocationDistributor();
		
		log.debug(String.format("A WindowContext is created."));
	}

	public IMessengerWindow getMessengerWindow() {
		return messengerWindow;
	}

	public IChannel getChannelByBuddyUid(String buddyUid) {
		return channelMap.get(buddyUid);
	}
	
	public Collection<String> getChannelBuddyUIDs() {
		return Collections.unmodifiableCollection(channelMap.keySet());
	}
	
	public void addOnboardChannel(ChannelContext channel) {
		channelMap.put(channel.getBuddy().getUid(), channel);
	}


	public Collection<ChannelContext> getOnboardChannels() {
		return Collections.unmodifiableCollection(channelMap.values());
	}

	public IChannel removeOnboardChannel(String userId){

		IChannel channel = channelMap.remove(userId);
		assert(channel != null);
		return channel;
	}

	public void closeWindow() {
		
		this.messengerWindow.closeWindow();
	}

	public void inviteBuddyToWindow(String userId) {
		this.messengerWindow.inviteMessengerUser(userId);
	}


	public void kickoutBuddyFromWindow(String userId) {
		this.messengerWindow.kickoutMessengerUser(userId);
	}

	
	public void sendQuickResponse(IChatletMessage... responseArray){
		this.messengerWindow.sendResponse(responseArray);
	}

	public Locale getLocale() {
		return this.messengerWindow.getLocale();
	}
	
	public IConnection getConnection() {
		return messengerWindow.getConnection().getConnectionContext();
	}

	public IApplication getApplicationContext() {
		return messengerWindow.getConnection().getApplication().getApplicationContext();
	}

	@Override
	public String getUid() {
		return windowUid;
	}
	
	public ChannelContext getDefaultChannel() {
		if((defaultChannel == null) && (this.channelMap.size() == 1)){
			defaultChannel = this.channelMap.values().iterator().next();
		}
		return defaultChannel;
	}
	
	public void setDefaultChannel(String defaultBuddyId){
		assert((defaultBuddyId != null ) && this.channelMap.containsKey(defaultBuddyId));
		this.defaultChannel = this.channelMap.get(defaultBuddyId);
	}

	

	@Override
	public IMessageSender getMessageSender() {
		
		return messengerWindow.getMessageSender();
	}

	@Override
	public UidType getUidType() {
		return UidType.WINDOW;
	}

	@Override
	public IMeeting getMeeting() {
		return this.meeting;
	}

	public void setMeeting(IMeeting meeting) throws WindowInOtherMeetingException{
	     meetingLock.lock();  // block until condition holds
	     try {
	    	 if(this.meeting == null){
	    		 this.meeting = meeting;
	    	 }else{
	    		 if(!this.meeting.getUid().equals(meeting.getUid()))
	    			 throw new WindowInOtherMeetingException("Cannot set a new meeting , when a meeting is existing", this.getUid(), meeting.getUid());
	    	 }
	     } finally {
	       meetingLock.unlock();
	     }
	}
	
	public void resetMeeting(){
	     meetingLock.lock();  // block until condition holds
	     try {
	    	 this.meeting = null;
	     } finally {
	       meetingLock.unlock();
	     }
	}
	

	
	@Override
	public IMeeting startMeetingWithBuddy(IEventErrorCallback handler, String... buddyUids) throws WindowInOtherMeetingException, IdentifierNotExistingException{
		
		if(this.meeting != null){
			throw new WindowInOtherMeetingException("Meeting("+ meeting.getUid() +") is already existing for window: " + this.getUid(),this.getUid(), this.meeting.getUid());
		}
		IMeeting meeting =  application.getMeetingStorage().createMeeting(windowUid);
		
		setMeeting(meeting);
		
		try {
			addBuddyToMeeting(handler, buddyUids);
		} catch (NoMeetingException e) {
			log.warn("Meeting should existing, and exception should not happening", e);
		}
		return meeting;
	}
	
	
	@Override
	public void addBuddyToMeeting(IEventErrorCallback handler, String... buddyUids) throws NoMeetingException, IdentifierNotExistingException{
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}

		if(buddyUids != null){
			for(String buddyUid : buddyUids){
				String connectionUid = UidHelper.parseConnectionUidFromBuddyUid(buddyUid);
				BuddyAddedToMeetingInvocation request = new BuddyAddedToMeetingInvocation(meeting.getUid(), buddyUid, this.getUid(), new Date());		
				application.getClusterInvocationDistributor().distributeConnectionRequest(handler, request, connectionUid);
			}
		}

	}
	
	
	@Override
	public IMeeting startMeetingWithWindow(IEventErrorCallback handler, String... windowUids)  throws WindowInOtherMeetingException, IdentifierNotExistingException{
		if(this.meeting != null){
			throw new WindowInOtherMeetingException("Meeting("+ meeting.getUid() +") is already existing for window: " + this.getUid(),this.getUid(), this.meeting.getUid());
		}
		IMeeting meeting =  application.getMeetingStorage().createMeeting(windowUid);
		
		setMeeting(meeting);
		
		try {
			addWindowToMeeting(handler, windowUids);
		} catch (NoMeetingException e) {
			log.warn("Meeting should existing, and exception should not happening", e);
		}
		return meeting;
	}

	@Override
	public void addWindowToMeeting(IEventErrorCallback handler, String... windowUids) throws NoMeetingException, IdentifierNotExistingException{
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}

		if(windowUids != null && windowUids.length > 0){
			for(String windowUid : windowUids){
				
				WindowAddedToMeetingInvocation request = new WindowAddedToMeetingInvocation(meeting.getUid(), windowUid, this.getUid(), new Date());		
				application.getClusterInvocationDistributor().distributeWindowRequest(handler, request, windowUid);
			}
		}

	}
	
	@Override
	public void joinMeeting(String meetingUid) throws WindowInOtherMeetingException, IdentifierNotExistingException{
		if (getMeeting() != null && getMeeting().getUid().equals(meetingUid)) {
			throw new WindowInOtherMeetingException("Meeting already existing", this.getUid(),  getMeeting().getUid());
		}
		
		OtherWindowAddedToMeetingInvocation invocation = new OtherWindowAddedToMeetingInvocation(meetingUid, getUid(), getUid(), new Date());
		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meetingUid);
		if(windowUidSet == null){
			throw new IdentifierNotExistingException(UidType.MEETING, meetingUid);
		}
		try {
			application.getClusterInvocationDistributor().distributeWindowRequest(null, invocation, windowUidSet.toArray(new String[windowUidSet.size()]));
		} catch (IdentifierNotExistingException e) {
			log.warn("Error when trying to send window added to meeting", e);
		}
		application.getMeetingStorage().addWindowsToMeeting(meetingUid, getUid());
	}	
	
	@Override
	public  void leaveMeeting(IEventErrorCallback handler) throws NoMeetingException {
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}
		
		application.getMeetingStorage().removeWindowsFromMeeting(meeting.getUid(), this.getUid());
		
		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meeting.getUid());
		if(windowUidSet != null && windowUidSet.size() > 0){
			OtherWindowLeftMeetingInvocation request = new OtherWindowLeftMeetingInvocation(meeting.getUid(), this.getUid(), this.getUid(), new Date());
			sendRequestToExistingWindowSet(handler, request, windowUidSet);
		}else{
			application.getMeetingEventListener().onMeetingStopped(this, this.getUid());
			application.getMeetingStorage().destoryMeeting(meeting.getUid());
		}
		
		meeting = null;
	}
	
	@Override
	public void kickoutWindowFromMeeting(IEventErrorCallback handler, String windowUid) throws NoMeetingException, IdentifierNotExistingException {
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}
		
		assert(meeting.getWindowUidSet().contains(windowUid));
		
		IClusterInvocation<IWindow> request = new WindowRemovedFromMeetingInvocation(new Date(), this.meeting.getUid(), this.getUid());
		
		requestDistributor.distributeWindowRequest(handler, request, windowUid);
		
	}
	
	
	
	private void sendRequestToExistingWindowSet(IEventErrorCallback handler, IClusterInvocation<IWindow> request, Set<String> existingWindowIdSet){
		HashSet<String> copyOfExistingWindowIdSet = new HashSet<String>(existingWindowIdSet);
		copyOfExistingWindowIdSet.remove(this.getUid());
		if(copyOfExistingWindowIdSet.size() > 0){
			try {
				requestDistributor.distributeWindowRequest(handler, request, copyOfExistingWindowIdSet.toArray(new String[copyOfExistingWindowIdSet.size()]));
			} catch (IdentifierNotExistingException e) {
				log.warn("Invalid identifier", e);
			}
		}
		
	}


	@Override
	public void stopMeeting(IEventErrorCallback handler) throws NoMeetingException {
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}
		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meeting.getUid());
		if(windowUidSet != null && windowUidSet.size() > 0){
			MeetingStoppedInvocation request = new MeetingStoppedInvocation(meeting.getUid(), this.getUid(), new Date());
			sendRequestToExistingWindowSet(handler, request, windowUidSet);
		}
		
		
		application.getMeetingStorage().destoryMeeting(meeting.getUid());
	}

	@Override
	public void fireMeetingEventToAllOtherWindows(IEvent event, IEventErrorCallback handler) throws NoMeetingException {
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}		
		Set<String> windowUidSet = application.getMeetingStorage().getReadOnlyWindowUidSet(meeting.getUid());
		sendRequestToExistingWindowSet(handler, new InMeetingEventInvocationWrapper(meeting.getUid(), event, new Date()), windowUidSet);

	}

	@Override
	public void fireMeetingEventToWindows(IEvent event, IEventErrorCallback handler, String... windowUids) throws NoMeetingException, IdentifierNotExistingException {
		if(this.meeting == null){
			throw new NoMeetingException("No meeting exists", this.getUid()); 
		}
		
		assert(windowUids != null && windowUids.length != 0);
		
		HashSet<String> windowUidSet = new HashSet<String>();
		
		for(String windowUid : windowUids){
			assert(application.getMeetingStorage().getReadOnlyWindowUidSet(meeting.getUid()).contains(windowUid));
			windowUidSet.add(windowUid);
		}
		
		sendRequestToExistingWindowSet(handler, new InMeetingEventInvocationWrapper(meeting.getUid(), event, new Date()), windowUidSet);
		
	}
	
	
	public IChatletApplication getChatletApplication(){
		return application;
	}

	
}
