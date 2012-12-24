package com.imseam.cdi.chatlet.deployment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class URLScanner
{

	private static final Log log = LogFactory.getLog(URLScanner.class);

   private final ClassLoader classLoader;
   
   public URLScanner(ClassLoader classLoader)
   {
      this.classLoader = classLoader;
   }

   protected void handle(String name, URL url, List<String> classes, List<URL> urls)
   {
      if (name.endsWith(".class"))
      {
         classes.add(filenameToClassname(name));
      }
      else if (name.equals(ChatAppBeanDeploymentArchive.META_INF_BEANS_XML))
      {
         urls.add(url);
      }
   }
   
   public void scanDirectories(File[] directories, List<String> classes, List<URL> urls)
   {
      for (File directory : directories)
      {
         handleDirectory(directory, null, classes, urls);
      }
   }

   public void scanResources(String[] resources, List<String> classes, List<URL> urls)
   {
      Set<String> paths = new HashSet<String>();
      
      for (String resourceName : resources)
      {
         try
         {
            Enumeration<URL> urlEnum = classLoader.getResources(resourceName);
            
            while (urlEnum.hasMoreElements())
            {
               String urlPath = urlEnum.nextElement().getFile();
               urlPath = URLDecoder.decode(urlPath, "UTF-8");
               
               if (urlPath.startsWith("file:"))
               {
                  urlPath = urlPath.substring(5);
               }
               
               if (urlPath.indexOf('!') > 0)
               {
                  urlPath = urlPath.substring(0, urlPath.indexOf('!'));
               }
               else
               {
                  File dirOrArchive = new File(urlPath);
                  
                  if ((resourceName != null) && (resourceName.lastIndexOf('/') > 0))
                  {
                     // for META-INF/beans.xml
                     dirOrArchive = dirOrArchive.getParentFile();
                  }
                  
                  urlPath = dirOrArchive.getParent();
               }
               
               paths.add(urlPath);
            }
         }
         catch (IOException ioe)
         {
            log.warn("could not read: " + resourceName, ioe);
         }
      }
      
      handle(paths, classes, urls);
   }

   protected void handle(Set<String> paths, List<String> classes, List<URL> urls)
   {
      for (String urlPath : paths)
      {
         try
         {
            log.trace("scanning: " + urlPath);
            
            File file = new File(urlPath);
            
            if (file.isDirectory())
            {
               handleDirectory(file, null, classes, urls);
            }
            else
            {
               handleArchiveByFile(file, classes, urls);
            }
         }
         catch (IOException ioe)
         {
            log.warn("could not read entries", ioe);
         }
      }
   }

   private void handleArchiveByFile(File file, List<String> classes, List<URL> urls) throws IOException
   {
      try
      {
         log.trace("archive: " + file);
         
         ZipFile zip = new ZipFile(file);
         Enumeration<? extends ZipEntry> entries = zip.entries();
         
         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            handle(name, classLoader.getResource(name), classes, urls);
         }
      }
      catch (ZipException e)
      {
         throw new RuntimeException("Error handling file " + file, e);
      }
   }

   private void handleDirectory(File file, String path, List<String> classes, List<URL> urls)
   {
      handleDirectory(file, path, new File[0], classes, urls);
   }

   private void handleDirectory(File file, String path, File[] excludedDirectories, List<String> classes, List<URL> urls)
   {
      for (File excludedDirectory : excludedDirectories)
      {
         if (file.equals(excludedDirectory))
         {
            log.trace("skipping excluded directory: " + file);
            
            return;
         }
      }
      
      log.trace("handling directory: " + file);
      
      for (File child : file.listFiles())
      {
         String newPath = (path == null) ? child.getName() : (path + '/' + child.getName());
         
         if (child.isDirectory())
         {
            handleDirectory(child, newPath, excludedDirectories, classes, urls);
         }
         else
         {
            try
            {
               handle(newPath, child.toURI().toURL(), classes, urls);
            }
            catch (MalformedURLException e)
            {
               log.error("Error loading file " + newPath);
            }
         }
      }
   }

   /**
    * Convert a path to a class file to a class name
    */
   public static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
}
   