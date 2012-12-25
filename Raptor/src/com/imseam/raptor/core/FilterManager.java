package com.imseam.raptor.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletFilter;


public class FilterManager {

	private static Log log = LogFactory.getLog(FilterManager.class);

	private static FilterManager instance = new FilterManager();
	
	private Map<String, IChatletFilter> filterMap = new HashMap<String, IChatletFilter>();
	
	
	
	private FilterManager()
	{
		log.debug("A FilterManager Object is created.");
	}
	
	public static FilterManager instance()
	{
		return instance;
	}
	
	public void addFilter(String name, IChatletFilter filter){
		if(filterMap.get(name) != null){
			log.warn("The filter " + name +" is already existing!");;
		}
		filterMap.put(name, filter);
	}
	
	public IChatletFilter getFilter(String filterName){
		return filterMap.get(filterName);
	}
	

}
