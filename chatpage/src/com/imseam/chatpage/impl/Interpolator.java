package com.imseam.chatpage.impl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatpage.context.ChatpageContext;



public class Interpolator {
	 private static final Log log = LogFactory.getLog(Interpolator.class);
	 private static Interpolator instance = new Interpolator();
	 
	   
	   public static Interpolator getInstance()
	   {
	      
	       return instance; //for unit testing
	      
	   }
	   
	   /**
	    * Replace all EL expressions in the form #{...} with their evaluated
	    * values.
	    * 
	    * @param string a template
	    * @return the interpolated string
	    */
	   public String interpolate(Locale locale, String string, Object... params) {
	      if ( params.length>10 ) 
	      {
	         throw new IllegalArgumentException("more than 10 parameters");
	      }
	      
	      if ( string.indexOf('#')>=0 )
	      {
	         string = interpolateExpressions(string, params);
	      }
	      if ( params.length>1 && string.indexOf('{')>=0 )
	      {
	         string = new MessageFormat(string, locale).format(params);
	      }
	      return string;
	   }


	   public String interpolate(String string, Object... params) {
		   return interpolate(null, string, params);
	   }	   
	   
	   private String interpolateExpressions(String string, Object... params)
	   {
	      StringTokenizer tokens = new StringTokenizer(string, "#{}", true);
	      StringBuilder builder = new StringBuilder(string.length());
	      while ( tokens.hasMoreTokens() )
	      {
	         String tok = tokens.nextToken();
	         if ( "#".equals(tok) && tokens.hasMoreTokens() )
	         {
	            String nextTok = tokens.nextToken();
	            if ( "{".equals(nextTok) )
	            {
	               String expression = "#{" + tokens.nextToken() + "}";
	               try
	               {
//	                  Object value = ChatPageManager.getInstance().getExpressionSolver().getValue(expression);
	            	  Object value = ChatpageContext.current().evaluateExpression(expression);
	                  if (value!=null) builder.append(value);
	               }
	               catch (Exception e)
	               {
	                  log.warn("exception interpolating string: " + string, e);
	               }
	               tokens.nextToken(); //the }
	            }
	            else 
	            {
	               int index;
	               try
	               {
	                  index = Integer.parseInt( nextTok.substring(0, 1) );
	                  if (index>=params.length) 
	                  {
	                     //log.warn("parameter index out of bounds: " + index + " in: " + string);
	                     builder.append("#").append(nextTok);
	                  }
	                  else
	                  {
	                     builder.append( params[index] ).append( nextTok.substring(1) );
	                  }
	               }
	               catch (NumberFormatException nfe)
	               {
	                  builder.append("#").append(nextTok);
	               }
	            }
	         }
	         else
	         {
	            builder.append(tok);
	         }
	      }
	      return builder.toString();
	   }	

}
