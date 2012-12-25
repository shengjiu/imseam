package com.imseam.raptor.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.config.util.ApplicationConfigHelper;

public class ImseamRaptorConfig{
	
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ImseamRaptorConfig.class);
	
	private final static String  location = "chatlet-config-location";
	
	private final static String propertyFileName = "imseam.raptor.config";
	
	private final static String sessionTimeOut = "session-timeout";
	
	private final static String channelTimeOut = "channel-timeout";
	
	private ApplicationConfigHelper config = null;
	
	private static ImseamRaptorConfig instance = new ImseamRaptorConfig();
	
	private ImseamRaptorConfig(){
		try{
			config = ApplicationConfigHelper.createConfig(propertyFileName);
		}catch(Exception exp){
			log.warn("cannot load the imseam.raptor.config file:" + propertyFileName);
		}
	}
	
	public static ImseamRaptorConfig instance(){
		return instance;
	}
	
	public long getDefaultSessionTimeOut(){
		if(config == null){
			return 300000l;
		}
		return Long.valueOf(config.getPropertyValue(sessionTimeOut));
	}
	
	public long getDefaultChannelTimeOut(){
		if(config == null){
			return 300000l;
		}
		return Long.valueOf(config.getPropertyValue(channelTimeOut));
	}
	
}
