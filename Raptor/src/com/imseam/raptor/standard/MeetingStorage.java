package com.imseam.raptor.standard;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IMeeting;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.chatlet.MeetingContext;
import com.imseam.raptor.chatlet.UidHelper;
import com.imseam.raptor.cluster.IMeetingStorage;

public class MeetingStorage implements IMeetingStorage {
	
	private static Log log = LogFactory.getLog(MeetingStorage.class);
	
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> meetingObjectMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Object>>();
	private ConcurrentHashMap<String, CopyOnWriteArraySet<String>> meetingWindowUidSetMap = new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>();
	private ConcurrentHashMap<String, IMeeting> meetingMap = new ConcurrentHashMap<String, IMeeting>();
	
	private IChatletApplication application = null;
	
	

	//private IChatletApplication application = null;
	

	@Override
	public void initApplication(IChatletApplication application) {
		this.application = application;

	}



	@Override
	public Set<String> getReadOnlyWindowUidSet(String meetingUid) {
		Set<String> meetingWindowUidSet = this.meetingWindowUidSetMap.get(meetingUid);
		if(meetingWindowUidSet == null){
			return null;
		}
		return Collections.unmodifiableSet(meetingWindowUidSet);
	}

	@Override
	public IMeeting createMeeting(String... windowUids) {
		String meetingUid = UidHelper.createNewMeetingUid();
		MeetingContext meeting = new MeetingContext(application, meetingUid);
		this.meetingObjectMap.put(meetingUid, new ConcurrentHashMap<String, Object>());
		CopyOnWriteArraySet<String> windowUidSet = new CopyOnWriteArraySet<String>();
		if(windowUids != null){
			for(String windowUid : windowUids){
				windowUidSet.add(windowUid);
			}
		}
		this.meetingWindowUidSetMap.put(meetingUid, windowUidSet);
		
		this.meetingMap.put(meetingUid, meeting);
		
		return meeting;
	}

	@Override
	public IMeeting getExistingMeeting(String meetingUid) {
		IMeeting meeting = this.meetingMap.get(meetingUid);
		if(meeting != null){
			return meeting;
		}
		Set<String> windowUidSet  = this.meetingWindowUidSetMap.get(meetingUid);
		if(windowUidSet != null){
			meeting = new MeetingContext(application, meetingUid);
			this.meetingMap.putIfAbsent(meetingUid, meeting);
			return meeting;
		}
		return null;
	}
	
	@Override
	public void destoryMeeting(String meetingUid) {
		this.meetingObjectMap.remove(meetingUid);
		this.meetingWindowUidSetMap.remove(meetingUid);
		this.meetingMap.remove(meetingUid);
	}


	@Override
	public void addWindowsToMeeting(String meetingUid, String... windowUids) {
		if(windowUids == null) return;
		
		Set<String> meetingWindowUidSet = this.meetingWindowUidSetMap.get(meetingUid);
		if(meetingWindowUidSet == null){
			log.warn("Trying to add windows(" + StringUtil.constructStringUsingArray(null, windowUids) + ") to a not existing meeting:" + meetingUid);
			return;
		}
		for(String windowUid : windowUids){
			meetingWindowUidSet.add(windowUid);
		}
		
	}

	@Override
	public void removeWindowsFromMeeting(String meetingUid,
			String... windowUids) {
		if(windowUids == null) return;
		
		Set<String> meetingWindowUidSet = this.meetingWindowUidSetMap.get(meetingUid);
		if(meetingWindowUidSet == null){
			log.warn("Trying to remove windows(" + StringUtil.constructStringUsingArray(null, windowUids) + ") to an not existing meeting:" + meetingUid);
			return;
		}
		for(String windowUid : windowUids){
			meetingWindowUidSet.add(windowUid);
		}
	}



	@Override
	public Object put(String meetingUid, String key, Object obj) {
		ConcurrentHashMap<String, Object> meetingObjectMap = this.meetingObjectMap.get(meetingUid);
		if(meetingObjectMap == null){
			log.warn(String.format("Trying to put object (%s : %s) to a not existing meeting(%s)", key, obj, meetingUid));
			return null;
		}
		return meetingObjectMap.get(key);
	}


	@Override
	public Object remove(String meetingUid, String key) {
		ConcurrentHashMap<String, Object> meetingObjectMap = this.meetingObjectMap.get(meetingUid);
		if(meetingObjectMap == null){
			log.warn(String.format("Trying to remove object (key :%s ) from a not existing meeting(%s)", key, meetingUid));
			return null;
		}
		return meetingObjectMap.remove(key);
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String meetingUid, String key) {
		ConcurrentHashMap<String, Object> meetingObjectMap = this.meetingObjectMap.get(meetingUid);
		if(meetingObjectMap == null){
			log.warn(String.format("Trying to get object (key :%s ) from a not existing meeting(%s)", key, meetingUid));
			return null;
		}
		return (T) meetingObjectMap.get(key);
	}


	@Override
	public Set<String> getKeySet(String meetingUid) {
		ConcurrentHashMap<String, Object> meetingObjectMap = this.meetingObjectMap.get(meetingUid);
		if(meetingObjectMap == null){
			log.warn(String.format("Trying to get key set from a not existing meeting(%s)", meetingUid));
			return null;
		}
		return  meetingObjectMap.keySet();
	}

}

//private ArrayList<EventListener> elementCreatedListenerList = new ArrayList<EventListener>();
//private ArrayList<EventListener> elementRemovedListenerList = new ArrayList<EventListener>();
//private ArrayList<EventListener> elementUpdatedListenerList = new ArrayList<EventListener>();

//private void invokeListeners(ArrayList<EventListener> listenerList, ElementEvent event){
//	for(EventListener listener : listenerList){
//		listener.invokeListener(event);
//	}
//}

//@Override
//public void onMeetingCreated(String meetingUid) {
//	if(meetingMap.putIfAbsent(meetingUid, new ConcurrentHashMap<String, Object>()) != null){
//		ExceptionUtil.createRuntimeException("The meeting was existing: " + meetingUid);
//	}
//}
//
//@Override
//public void onMeetingDestoryed(String meetingUid) {
//	meetingMap.remove(meetingUid);
//}

//@SuppressWarnings("unchecked")
//@Override
//public <T> T put(String meetingUid, String key, T obj) {
//	ConcurrentHashMap<String, Object> meetingObjectMap = meetingMap.get(meetingUid);
//	if(meetingObjectMap == null){
//		ExceptionUtil.createRuntimeException("The meeting was not existing: " + meetingUid);
//	}
//	T oldObj = (T)meetingObjectMap.put(key, obj);
//	if(oldObj == null){
//		invokeListeners(elementCreatedListenerList, new ElementEvent(meetingUid, key));
//	}else{
//		invokeListeners(elementUpdatedListenerList, new ElementEvent(meetingUid, key));
//	}
//	return oldObj;
//}
//
//@SuppressWarnings("unchecked")
//@Override
//public <T> T remove(String meetingUid, String key) {
//	ConcurrentHashMap<String, Object> meetingObjectMap = meetingMap.get(meetingUid);
//	if(meetingObjectMap == null){
//		ExceptionUtil.createRuntimeException("The meeting was not existing: " + meetingUid);
//	}
//	T oldObj = (T)meetingObjectMap.remove(key);
//	invokeListeners(elementRemovedListenerList, new ElementEvent(meetingUid, key));
//	return oldObj;
//}
//
//@SuppressWarnings("unchecked")
//@Override
//public <T> T get(String meetingUid, String key) {
//	ConcurrentHashMap<String, Object> meetingObjectMap = meetingMap.get(meetingUid);
//	if(meetingObjectMap == null){
//		ExceptionUtil.createRuntimeException("The meeting was not existing: " + meetingUid);
//	}
//	return (T)meetingObjectMap.get(key);
//}
//
//@Override
//public Set<String> getKeySet(String meetingUid) {
//	ConcurrentHashMap<String, Object> meetingObjectMap = meetingMap.get(meetingUid);
//	if(meetingObjectMap == null){
//		ExceptionUtil.createRuntimeException("The meeting was not existing: " + meetingUid);
//	}
//	return meetingObjectMap.keySet();
//}
//
//@Override
//public void addListner(Object listener) {
//	assert(listener != null);
//	Method[] methods = listener.getClass().getMethods();
//	
//	for(Method method : methods){
//		if(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(ElementEvent.class)){
//			if(method.isAnnotationPresent(ElementCreated.class)){
//				this.elementCreatedListenerList.add(new EventListener(method, listener));
//			}
//			if(method.isAnnotationPresent(ElementRemoved.class)){
//				this.elementRemovedListenerList.add(new EventListener(method, listener));					
//			}
//
//			if(method.isAnnotationPresent(ElementUpdated.class)){
//				this.elementUpdatedListenerList.add(new EventListener(method, listener));
//			}
//		}
//	}
//
//}

//private class EventListener{
//private Method method = null;
//private Object listener = null;
//
//EventListener(Method method, Object listener){
//	this.method = method;
//	this.listener = listener;
//}
//
//void invokeListener(ElementEvent event){
//	try{
//		method.invoke(listener, event);
//	}catch(Exception exp){
//		ExceptionUtil.wrapRuntimeException(exp);
//	}
//}
//
//}