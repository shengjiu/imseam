package com.imseam.raptor.threading;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.threading.annotation.QueuedTask;

public class ReflectedQueuedTask implements IQueuedTask {
	private static Log log = LogFactory.getLog(ReflectedQueuedTask.class);

	private Object originalObject;

	private Method method = null;

	private Object[] parameters;

	public ReflectedQueuedTask(Object originalObject, String methodName,
			Object... parameters) {
		log.info(String.format("Creating a GenericQueuedTask Object: originalObject(%s), methodName(%s)", originalObject, methodName));
		if(originalObject == null){
			ExceptionUtil.createRuntimeException("The to be queued Object is null.");
		}
	
		if(StringUtil.isNullOrEmptyAfterTrim(methodName)){
			ExceptionUtil.createRuntimeException("The to be queued task's method is null");
		}
		
		this.originalObject = originalObject;
		this.parameters = parameters;
		methodName = methodName.trim();

		
			Class clazz = originalObject.getClass();
			
			level1:
			for (Method m : clazz.getMethods()){
				if(m.getName().equals(methodName) && (m.isAnnotationPresent(QueuedTask.class))){
					
					Class<?>[] methodParameterTypes = m.getParameterTypes();
					
					if(((methodParameterTypes == null) && (parameters != null)) ||
							((methodParameterTypes != null) && (parameters == null))){
						continue;
					}
					
					if(methodParameterTypes.length != parameters.length)
						continue;
					
					for(int i = 0; i < parameters.length; i++){
						if(!methodParameterTypes[i].isAssignableFrom(parameters[i].getClass())){
							break level1;
						}
					}
					method = m;
				}
			}

			if(method == null){
				ExceptionUtil.createRuntimeException(String.format("The Queued Object cannot find matched method (%s)", methodName));
			}
			
		
		log.info(String.format("A GenericQueuedTask: %s.%s is created", originalObject.getClass().getName(), method.getName()));
	}

	public void perform() {
		log.info(String.format("Invoking: %s.%s", originalObject.getClass().getName(), method.getName()));
		try {
			method.invoke(originalObject, parameters);
		} catch (Exception exp) {

			ExceptionUtil.wrapRuntimeException(String.format(
					"The method (%s) throw exceptions", method.getName()), exp);
		}
		log.info(String.format("Invoking: %s.%s finished", originalObject.getClass().getName(), method.getName()));

	}

}
