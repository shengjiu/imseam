package com.imseam.raptor.internalization;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IUserRequest;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.common.util.StringUtil;

public class ResourceBundleHelper {
	private static Log log = LogFactory.getLog(ResourceBundleHelper.class);
	
	private static ResourceBundleHelper instance = null;
	private String base = null;
	
	public void initResourceBundle(String base){
		instance = new ResourceBundleHelper(base);
	}
	
	private ResourceBundleHelper(String base){
		assert(!StringUtil.isNullOrEmptyAfterTrim(base));
		this.base = base;
	}
	
	public static ResourceBundleHelper getInstance(){
		if(instance == null){
			ExceptionUtil.createRuntimeException("The resource bundle is not initialized. Please call initResourceBundle(baseName first");
		}
		return instance;
	}
	
	public String getResourceString(Locale locale, String key, IUserRequest chatletRequest){
		assert(!StringUtil.isNullOrEmptyAfterTrim(key));
		String parameteredValue = ResourceBundle.getBundle(base, locale).getString(key);
		assert(!StringUtil.isNullOrEmptyAfterTrim(parameteredValue));
		return parameteredValue;
	}
	
	public String getExceptionMessageWithUniqueID(Locale locale, String uniqueID, String expKey, IUserRequest chatletRequest){
		String uniqueMsg = getResourceString(locale, ResourceStringEnum.customer_readable_exception.toString(), chatletRequest);

		uniqueMsg = String.format(uniqueMsg, uniqueID); 
		
		String customerMsg = getResourceString(locale, expKey, chatletRequest);

		return customerMsg + "\n" + uniqueMsg;
	}

}
