package com.imseam.test;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringSerializerUtil {
	
	public static String fromDate(Date date){
		return "date" + ":::" +date.toString();
	}
	
	public static String fromMap(Map map){
		String result = "map";
		for(Object key : map.keySet()){
			result += ":::" + key + ":::" + map.get(key);
		}
		return result;
	}

	public static String fromList(List list){
		String result = "list";
		for(Object obj : list){
			result += ":::"+ obj;
		}
		return result;
	}
	
	public static String fromSet(Set set){
		String result = "set";
		for(Object obj : set){
			result += ":::"+ obj;
		}
		return result;
	}
	
	
	public static String from(Object obj){
		if(obj instanceof Date){
			return fromDate((Date)obj);
		}
		if(obj instanceof Map){
			return fromMap((Map)obj);
		}

		if(obj instanceof Set){
			return fromSet((Set)obj);
		}

		if(obj instanceof List){
			return fromList((List)obj);
		}

		
		return null;
	}
	public static boolean equal(String str, Map map){
		return fromMap(map).equals(str);	}

	public static boolean equal(String str, Set set){
		return fromSet(set).equals(str);
	}
	
	public static boolean equal(String str, List list){
		return fromList(list).equals(str);
		
	}

	public static boolean equal(String str, Date date){
		return fromDate(date).equals(str);
	}
	
	public static Object of(String str){
		if(str.startsWith("date")){
			String [] temps = str.split(":::");
			return new Date(Date.parse(temps[1]));
			
		}
		
		if(str.startsWith("map")){
			String [] temps = str.split(":::");
			int count = (temps.length -1 )/2;
			Map result = new HashMap();
			for(int i = 0; i < count; i++){
				result.put(temps[1+i*2], temps[1+i*2 + 1]);
			}
			return result;
		}
		if(str.startsWith("list")){
			String [] temps = str.split(":::");
			List result = new ArrayList();
			for(int i = 1; i < temps.length; i++){
				result.add(temps[i]);
			}
			return result;
		}
		if(str.startsWith("set")){
			String [] temps = str.split(":::");
			Set result = new HashSet();
			for(int i = 1; i < temps.length; i++){
				result.add(temps[i]);
			}
			return result;
			
		}
			
		return str;
	}

	
}
