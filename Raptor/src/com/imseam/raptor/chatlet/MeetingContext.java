package com.imseam.raptor.chatlet;

import java.util.Iterator;
import java.util.Set;

import com.imseam.chatlet.IMeeting;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IMeetingStorage;

public class MeetingContext implements IMeeting {
	
//	private static Log log = LogFactory.getLog(MeetingContext.class);
		
	private final String meetingUid;	
//	private final String meetingPath;
	
	private IMeetingStorage meetingStorage;
	
	
//	private final String PATH_MEETINGID_TO_WINDOWIDSET_MAPPING ="/imseam/MEETINGID_TO_WINDOWIDSET_MAPPING/";
	
	public MeetingContext(IChatletApplication application, String meetingUid) {
		this.meetingUid = meetingUid;
		meetingStorage = application.getMeetingStorage();
	}

//	public MeetingContext(IChatletApplication application, String meetingUid, Set<String> existingWindowIdSet) {
//		meetingStorage = application.getMeetingStorage();
//		this.meetingUid = meetingUid;
//		if(existingWindowdSet != null && existingWindowIdSet.size() > 0){
//			String [] windowUids = existingWindowIdSet.toArray(new String[existingWindowIdSet.size()]);
//			meetingStorage(meetingUid, windowUids);
//		}
//	}

	
//	private String getKeyForMeetingId2WindowIdSetMapping(String meetingId){
//		return PATH_MEETINGID_TO_WINDOWIDSET_MAPPING + meetingId;
//	}
//	

//	public static Set<String> getWindowIdSet(String path){
//		
//		return ClusterStorage.getInstance().get(path, ClusterStorage.WINDOWSET_KEY);
//	}
//	
//	public static IMeeting createMeetingContext(String... windowIds)throws WindowInOtherMeetingException {
//		
//		MeetingContext meetingContext = new MeetingContext(UUID.randomUUID().toString());
//		Set<String> windowSet =  new HashSet<String>();
//		for(String windowId : windowIds){
//			windowSet.add(windowId);
//		}
//		ClusterStorage.getInstance().put(meetingContext.meetingPath, ClusterStorage.WINDOWSET_KEY, windowSet);
//
//		return meetingContext;
//	}
	

//	public static IMeeting createLocalMeetingContext(String meetingId, String... windowIds){
//		
//		return new MeetingContext(meetingId);
//
//	}
	
	
	
	
	@Override
	public Set<String> getWindowUidSet() {
		
		return meetingStorage.getReadOnlyWindowUidSet(meetingUid);	
	}	

	
	
	public Object getAttribute(String key) {
		return meetingStorage.get(meetingUid, key);
	}

	public Set<String> getAttributeNames() {
		return meetingStorage.getKeySet(meetingUid);
	}

	public Object removeAttribute(String key) {
		return meetingStorage.remove(meetingUid, key);
	}

	public void setAttribute(String key, Object obj) {
		meetingStorage.put(meetingUid, key, obj);
	}

	public void removeAllAttributes() {
		Set<String> attributeNames = this.getAttributeNames();
		if(attributeNames != null){
			for(String key : attributeNames){
				this.removeAttribute(key);
			}
		}
	}
	
	//to be overrided
	public void flush(){}


	@Override
	public String getUid() {
		return meetingUid;
	}

	@Override
	public UidType getUidType() {
		return UidType.MEETING;
	}

	@Override
	public Iterator<String> iterator() {
		return getAttributeNames().iterator();
	}
	
	

}
