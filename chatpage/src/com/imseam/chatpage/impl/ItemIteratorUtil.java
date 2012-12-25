package com.imseam.chatpage.impl;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.imseam.common.util.ExceptionUtil;

public class ItemIteratorUtil {

	public static int getForEachItemsSize(Object src){
		if (src == null) {
			return 0;
		} else if (src instanceof Collection) {
			return ((Collection<?>) src).size();
		} else if (src instanceof Map) {
			return ((Map<?, ?>) src).size();
		} else if (src.getClass().isArray()) {
			return Array.getLength(src);
		} else {
			ExceptionUtil.createRuntimeException("Must evaluate to a Collection, Map, Array, or null.");
		}	
		return 0;
	}
	
	
	public static Object getForEachItem(Object src, int index){
		if (src == null) {
			return null;
		} else if (src instanceof Collection) {
			Collection<?> srcCollection =(Collection<?>) src;
			if(index < 0 || index > srcCollection.size()){
				return null;
			}
			int count = 0;
			for(Object itemObj : srcCollection){
				if(count == index){
					return itemObj;
				}
				count++;
			}
			return null;
		} else if (src instanceof Map) {
			Map<?, ?> srcMap =(Map<?, ?>) src;
			if(index < 0 || index > srcMap.size()){
				return null;
			}
			int count = 0;
			for(Object entry : srcMap.entrySet()){
				if(count == index){
					return entry;
				}
				count++;
			}
			return null;
		} else if (src.getClass().isArray()) {

			if(index < 0 || index > Array.getLength(src)){
				return null;
			}
			
			return Array.get(src, index);

		} else {
			ExceptionUtil.createRuntimeException("Must evaluate to a Collection, Map, Array, or null.");
		}	
		return null;
	}


	public static Iterator<?> toIterator(Object src) {
		if (src == null) {
			return null;
		} else if (src instanceof Collection) {
			return ((Collection<?>) src).iterator();
		} else if (src instanceof Map) {
			return ((Map<?, ?>) src).entrySet().iterator();
		} else if (src.getClass().isArray()) {
			return new ArrayIterator(src);
		} else {
			ExceptionUtil.createRuntimeException("Must evaluate to a Collection, Map, Array, or null.");
		}
		
		return null;
	}

	public static class ArrayIterator implements Iterator<Object> {

		protected final Object array;

		protected int i;

		protected final int len;

		public ArrayIterator(Object src) {
			this.i = 0;
			this.array = src;
			this.len = Array.getLength(src);
		}

		public boolean hasNext() {
			return this.i < this.len;
		}

		public Object next() {
			return Array.get(this.array, this.i++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	
}
