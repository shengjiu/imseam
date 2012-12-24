package com.imseam.cdi.chatlet;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import com.imseam.common.util.ClassUtil;
import com.imseam.common.util.ExceptionUtil;


public class ChatletCDIAnnotation implements Annotation, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9025666788300292699L;
	private transient Class<? extends Annotation> annotationType;

	@SuppressWarnings("unchecked")
	protected ChatletCDIAnnotation(String className) {
		try {
			annotationType = ClassUtil.loadClass(className);
		} catch (ClassNotFoundException e) {
			ExceptionUtil.wrapRuntimeException(e);
		}
	}
	
	protected ChatletCDIAnnotation(Class<? extends Annotation> annotationClass) {
		annotationType = annotationClass;
	}

	public Class<? extends Annotation> annotationType() {
		return annotationType;
	}

	public static ChatletCDIAnnotation getAnnotation(String className){
		return new ChatletCDIAnnotation(className);
	}

	public static ChatletCDIAnnotation getAnnotation(Class<? extends Annotation> annotationClass){
		return new ChatletCDIAnnotation(annotationClass);
	}

}
