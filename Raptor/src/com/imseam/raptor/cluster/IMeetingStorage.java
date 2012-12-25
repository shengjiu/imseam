package com.imseam.raptor.cluster;

import java.util.Set;

import com.imseam.chatlet.IMeeting;
import com.imseam.raptor.IChatletApplication;

/**
 * Only available to the server where meeting is running 
 * @author shengjiu
 *
 */
public interface IMeetingStorage{
	
	IMeeting createMeeting(String...windowUids);
	
	IMeeting getExistingMeeting(String meetingUid);
	
	void destoryMeeting(String meetingUid);
	
	Object put(String meetingUid, String key, Object obj);
	
	Object remove(String meetingUid, String key);
	
	<T> T get(String meetingUid, String key);
	
	Set<String> getReadOnlyWindowUidSet(String meetingUid);
	
	void addWindowsToMeeting(String meetingUid, String ... windowUids);
	
	void removeWindowsFromMeeting(String meetingUid, String ... windowUids);
	
	Set<String> getKeySet(String meetingUid);
	
	void initApplication(IChatletApplication application);
}
