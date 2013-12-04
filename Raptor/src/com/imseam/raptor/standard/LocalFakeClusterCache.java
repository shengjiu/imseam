package com.imseam.raptor.standard;

import java.util.concurrent.ConcurrentHashMap;

import com.imseam.cluster.IClusterTransaction;
import com.imseam.cluster.TimeoutForAcquireLockException;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.cluster.IRaptorClustercache;


public class LocalFakeClusterCache implements IRaptorClustercache {
	
	private ConcurrentHashMap<String, Object> clusterObjectMap = new ConcurrentHashMap<String, Object>();
	
//	private ArrayList<EventListener> elementCreatedListenerList = new ArrayList<EventListener>();
//	private ArrayList<EventListener> elementRemovedListenerList = new ArrayList<EventListener>();
//	private ArrayList<EventListener> elementUpdatedListenerList = new ArrayList<EventListener>();
//
//
//
//	
//	private void invokeListeners(ArrayList<EventListener> listenerList, ElementEvent event){
//		for(EventListener listener : listenerList){
//			listener.invokeListener(event);
//		}
//	}
	
	@Override
	public <T>void put(String key, T obj) {
		@SuppressWarnings("unchecked")
		T oldObj = (T)clusterObjectMap.put(key, obj);
//		if(oldObj == null){
//			invokeListeners(elementCreatedListenerList, new ElementEvent(key));
//		}else{
//			invokeListeners(elementUpdatedListenerList, new ElementEvent(key));
//		}
//		return oldObj;
	}

	@Override
	public <T> T putIfAbsent(String key, T obj) {
		
		T oldObj = (T)clusterObjectMap.putIfAbsent(key, obj);
		if(oldObj == null){
//			invokeListeners(elementCreatedListenerList, new ElementEvent(key));
		}
		return oldObj;
	}

	@Override
	public void remove(String key) {
		
		clusterObjectMap.remove(key);
//		invokeListeners(elementRemovedListenerList, new ElementEvent(key));
//		return oldObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) clusterObjectMap.get(key);
	}

//	@Override
//	public void addListner(Object listener) {
//		assert(listener != null);
//		Method[] methods = listener.getClass().getMethods();
//		
//		for(Method method : methods){
//			if(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(ElementEvent.class)){
//				if(method.isAnnotationPresent(ElementCreated.class)){
//					this.elementCreatedListenerList.add(new EventListener(method, listener));
//				}
//				if(method.isAnnotationPresent(ElementRemoved.class)){
//					this.elementRemovedListenerList.add(new EventListener(method, listener));					
//				}
//
//				if(method.isAnnotationPresent(ElementUpdated.class)){
//					this.elementUpdatedListenerList.add(new EventListener(method, listener));
//				}
//			}
//		}
//	}
//
//	private class EventListener{
//		private Method method = null;
//		private Object listener = null;
//		
//		EventListener(Method method, Object listener){
//			this.method = method;
//			this.listener = listener;
//		}
//		
//		void invokeListener(ElementEvent event){
//			try{
//				method.invoke(listener, event);
//			}catch(Exception exp){
//				ExceptionUtil.wrapRuntimeException(exp);
//			}
//		}
//		
//	}

	@Override
	public void init(IChatletApplication application) {
	}



@Override
public void lock(String... keys) throws TimeoutForAcquireLockException {
	// TODO Auto-generated method stub
	
}

@Override
public void optimisticLock(String... keys) {
	// TODO Auto-generated method stub
	
}

@Override
public void unlock(String... keys) {
	// TODO Auto-generated method stub
	
}

@Override
public IClusterTransaction startTransaction() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void releaseToPoolWithRollback() {
	// TODO Auto-generated method stub
	
}

@Override
public void releaseToPoolWithCommit() {
	// TODO Auto-generated method stub
	
}




}
