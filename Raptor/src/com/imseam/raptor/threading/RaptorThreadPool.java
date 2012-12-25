package com.imseam.raptor.threading;

import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RaptorThreadPool{
   private static final Log log = LogFactory.getLog(RaptorThreadPool.class);
   private PriorityBlockingQueue q = new PriorityBlockingQueue();
   private ExtendedThreadPoolExecutor executor = new ExtendedThreadPoolExecutor(10, 100, 120, TimeUnit.SECONDS, q);
   
   private static RaptorThreadPool instance = new RaptorThreadPool();
   
   public static RaptorThreadPool getInstance(){
	   return instance;
   }
   
   public FutureTask addQueue(RaptorTaskQueue queue){
	   return executor.submit(queue);
   }
   
}
