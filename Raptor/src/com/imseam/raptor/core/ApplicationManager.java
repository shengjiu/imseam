package com.imseam.raptor.core;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;

public class ApplicationManager {

	private static Log log = LogFactory.getLog(ApplicationManager.class);

	private static ApplicationManager instance = new ApplicationManager();
	
    private HashMap<String, IChatletApplication> applicationMap = new HashMap<String, IChatletApplication>();
    
	private ApplicationManager()
	{
		log.debug("The ApplicationManager Class is instantiated.");
	}
	
	public static ApplicationManager instance()
	{
		return instance;
	}
	
	public void addApplication(String name, IChatletApplication application){
		assert(!StringUtil.isNullOrEmptyAfterTrim(name));
		assert(application != null);
		if(applicationMap.get(name) != null){
			ExceptionUtil.createRuntimeException(String.format("The application (%s) is already existing!", name));
		}
		applicationMap.put(name, application);
	}
	
	public IChatletApplication getApplication(String name)
	{
		assert(!StringUtil.isNullOrEmptyAfterTrim(name));
		IChatletApplication application = applicationMap.get(name);
		
		if(application == null){
			log.warn(String.format("The application (%s) is not existing!", name));
		}
		return application;
	}
	
	public Collection<IChatletApplication> getAllApplications(){
		return applicationMap.values();
	}
	
	public void removeAllApplications(){
		this.applicationMap.clear();
	}

}
