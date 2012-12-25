package com.imseam.common.util;

public class StringUtil {

	public static boolean isNullOrEmpty(String str){
		
		if(str == null) return true;
		
		if(str.equals("")) return true;
		
		return false;
	}

	public static boolean isNullOrEmptyAfterTrim(String str){
		
		if(str == null) return true;
		
		return isNullOrEmpty(str);

	}
	
	public static String constructStringUsingArray(String seperator, String ... strings){
		if(strings == null) return null;
		if(seperator == null){
			seperator = ", ";
		}
		String output = null;
		for(String string : strings){
			if(output == null){
				output = string;
			}else{
				output = seperator + string;
			}
		}
		return output;
	}
	
}
