package com.imseam.config.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.common.util.StringUtil;



public class ApplicationConfigHelper{

	private static final Log log = LogFactory.getLog(ApplicationConfigHelper.class);
	
	private String propertyFileName = null;
	
	private Properties properties = null;
	
	private ApplicationConfigHelper(String propertyFileSystemPropertyKey){
		
		this.propertyFileName = System.getProperty(propertyFileSystemPropertyKey);
		
		
		log.info("Loading property file: " + propertyFileName);
		
		reloadProperties();
		
	}
	
	public static ApplicationConfigHelper createConfig(String propertyFileSystemPropertyKey){
		assert(!StringUtil.isNullOrEmptyAfterTrim(propertyFileSystemPropertyKey));
		return new ApplicationConfigHelper(propertyFileSystemPropertyKey);
	}
	


	public String getPropertyValue(String key){
		return this.properties.getProperty(key);
	}
	

	
	public boolean reloadProperties(){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertyFileName));
		} catch (IOException e) {
			log.error("Cannot load property file: " + propertyFileName, e);
			return false;
		}
		this.properties = properties;
		return true;
	}
	
}
