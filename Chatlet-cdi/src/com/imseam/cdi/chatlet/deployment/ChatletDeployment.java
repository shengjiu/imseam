package com.imseam.cdi.chatlet.deployment;

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;

import com.imseam.chatlet.IApplication;

public class ChatletDeployment implements Deployment
{
   
   private final ChatAppBeanDeploymentArchive chatAppBeanDeploymentArchive;
   private final Collection<BeanDeploymentArchive> beanDeploymentArchives;
   private final ServiceRegistry services;
   private final Iterable<Metadata<Extension>> extensions;

   public ChatletDeployment(IApplication application, Bootstrap bootstrap)
   {
      this.chatAppBeanDeploymentArchive = new ChatAppBeanDeploymentArchive(application, bootstrap);
      this.beanDeploymentArchives = new ArrayList<BeanDeploymentArchive>();
      this.beanDeploymentArchives.add(chatAppBeanDeploymentArchive);
      this.services = new SimpleServiceRegistry();
      this.extensions = bootstrap.loadExtensions(Thread.currentThread().getContextClassLoader());
   }

   public Collection<BeanDeploymentArchive> getBeanDeploymentArchives()
   {
      return beanDeploymentArchives;
   }

   public ServiceRegistry getServices()
   {
      return services;
   }

   public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass)
   {
      return chatAppBeanDeploymentArchive;
   }
   
   public ChatAppBeanDeploymentArchive getChatAppBeanDeploymentArchive()
   {
      return chatAppBeanDeploymentArchive;
   }
   
   public Iterable<Metadata<Extension>> getExtensions()
   {
      return extensions;
   }

}
