package com.imseam.cdi.chatlet.ext;

import java.lang.annotation.Annotation;

import junit.framework.TestCase;

import com.imseam.cdi.chatlet.ChatletCDIAnnotation;


public class ChatletCDIAnnotationTest extends TestCase {
	
	public void testAnnotation(){
		
		ChatletCDIAnnotation annotation = ChatletCDIAnnotation.getAnnotation("com.imseam.cdi.chatlet.ext.TestCDIAnnotation");
		
		this.assertTrue(annotation instanceof Annotation);
		
		Class<? extends Annotation> annotationType = annotation.annotationType();
		
		this.assertTrue(annotation instanceof Annotation);
		
		annotation = ChatletCDIAnnotation.getAnnotation(TestCDIAnnotation.class);
		
		this.assertTrue(annotation instanceof Annotation);
		
		
	}

}
