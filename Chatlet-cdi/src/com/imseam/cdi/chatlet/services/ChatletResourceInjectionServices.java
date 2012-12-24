package com.imseam.cdi.chatlet.services;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.injection.spi.ResourceInjectionServices;
import org.jboss.weld.injection.spi.helpers.AbstractResourceServices;

public class ChatletResourceInjectionServices extends AbstractResourceServices implements ResourceInjectionServices
{
	   
	   private Context context;
	   
	   public ChatletResourceInjectionServices()
	   {
	      try
	      {
	         context = new InitialContext();
	      }
	      catch (NamingException e)
	      {
	         throw new IllegalStateException("Error creating JNDI context", e);
	      }
	   }
	   
	   @Override
	   protected Context getContext()
	   {
	      return context;
	   }

}
