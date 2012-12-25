package com.imseam.annotation;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Invokable {
	private static Log log = LogFactory.getLog(Invokable.class);

	Object object;

	Method method;

	public Invokable(Object object, Method method) {
		this.object = object;
		this.method = method;
	}

	public void invoke(Object... params) {
		try {
			method.invoke(object, params);
		} catch (Exception exp) {
			log.error(String.format(
					"An error occured when trying to invoke %s method on %s",
					method.getName(), object.getClass()), exp);
		}
	}
	
//	public static <T extends Annotation> Invokable createInvokableByAnnotation(Object object, Class<T> annotationClass, Class<?>...paramTypes){
//		
//		Class clazz = object.getClass();
//		for (Method m : clazz.getMethods()){
//			T annotation = m.getAnnotation(annotationClass);
//			
//			if(annotation != null){
//				EventTypeEnum eventType = annotation.getClass().eventType();
//				if(eventType == null){
//					ExceptionUtil.createRuntimeException(
//							String.format("The eventType is a required attribute for method (%s) in class(%s).", m.getName(), listener.getClass()));
//				}
//				
//				Class<?> []methodParameterTypes = m.getParameterTypes();
//				
//				if((methodParameterTypes.length != 1)
//						|| !eventType.getEventObjectClass().isAssignableFrom(methodParameterTypes[0])){
//					ExceptionUtil.createRuntimeException(
//							String.format("The method (%s(%s)) in class(%s) is NOT defined as required by the (%s) to handle %s.", 
//									m.getName(), 
//									methodParameterTypes[0].getSimpleName(), 
//									listener.getClass().getSimpleName(), 
//									eventType,  
//									eventType.getEventObjectClass().getSimpleName()));
//				}
//				
//				List<Invokable> listenerList = eventListenerMap.get(eventType);
//				
//				if(listenerList == null){
//					listenerList = new ArrayList<Invokable>();
//					eventListenerMap.put(eventType, listenerList);
//				}
//		return null;
//	}
}
