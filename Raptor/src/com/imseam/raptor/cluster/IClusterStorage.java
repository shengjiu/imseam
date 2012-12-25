package com.imseam.raptor.cluster;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.imseam.chatlet.IInitable;
import com.imseam.raptor.IChatletApplication;

public interface IClusterStorage{
	
	<T> T put(String key, T obj);
	
	<T> T putIfAbsent(String key, T obj);
	
	<T>  T remove(String key);
	
	<T> T get(String key);
	
	void addListner(Object listener);
	
	void initApplication(IChatletApplication application);
	
	

	@Retention(RUNTIME)
	@Target(METHOD)
	@interface ElementCreated{ 
	}

	@Retention(RUNTIME)
	@Target(METHOD)
	@interface ElementRemoved{ 
	}

	@Retention(RUNTIME)
	@Target(METHOD)
	@interface ElementUpdated{ 
	}

	
	public class ElementEvent{
		private String elementKey;
		
		public ElementEvent(String elementKey){
			this.elementKey = elementKey;
		}
		
		public String getElementKey(){
			return elementKey;
		}
	}

}
