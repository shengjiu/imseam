package com.imseam.chatpage.el;


public abstract class ELUtil {
	
	public static boolean isLiteralText(String expr){
		if(expr == null ) return true;
		
		return expr.indexOf("#{") <0;
	}
	
}
