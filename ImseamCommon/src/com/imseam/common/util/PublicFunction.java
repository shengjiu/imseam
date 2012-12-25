package com.imseam.common.util;


import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublicFunction {
	
	private static Log log = LogFactory.getLog(PublicFunction.class);
	
	
    public long StringTolong(String instr)
    {
        Long a = new Long(instr);
        long xp = a.longValue();
        return xp;
    }
    
    
    //Verify E-mail format
    public boolean validateEmailFormat (String email) {
    	
    	boolean isValid = false;
    	String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    	CharSequence inputStr = email;


//    	Make the comparison case-insensitive.

    	try{
	    	Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
	    	Matcher matcher = pattern.matcher(inputStr);
	    	if(matcher.matches()){
		    	isValid = true;
		    }
    	}catch(Exception exp){
    		// do nothing
    	}
    	return isValid;
   	}
    
   
    
    public String randomPassword() {
        char[] c = { '1', '2', '3', '4', '5', '6', '7', '8', '9',  'q',
                'w', 'e', 'r', 't', 'y', 'u', 'i',  'p', 'a', 's', 'd',
                'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','P',
                'Q','R','S','T','U','V','W','X','Y','Z'};
        Random random = new Random(); 
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            sb.append(c[Math.abs(random.nextInt()) % c.length]);
        }
        return sb.toString();
    }
    
    public static void main(String [] args){
    	PublicFunction pf = new PublicFunction();
    	System.out.println("shengjiu.wang@gmail.com :"+ pf.validateEmailFormat("shengjiu.wang@gmail.com"));
    	System.out.println("shengjiu. wang@gmail.com : " +pf.validateEmailFormat("shengjiu. wang@gmail.com"));
    	System.out.println("shengjiu.wang@gmail.com.co: " + pf.validateEmailFormat("shengjiu.wang@gmail.com.co"));
    	System.out.println("shengjiu@gmail.com: " + pf.validateEmailFormat("shengjiu@gmail.com"));
    	System.out.println("shengjiu_gmail.com: " + pf.validateEmailFormat("shengjiu_gmail.com"));
    	System.out.println("shengjiu@gmailcom: " + pf.validateEmailFormat("shengjiu@gmailcom"));
    }

}
