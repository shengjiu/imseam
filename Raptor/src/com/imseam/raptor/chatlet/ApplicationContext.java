package com.imseam.raptor.chatlet;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IEventErrorCallback;
import com.imseam.chatlet.IWindow;
import com.imseam.chatlet.config.ChatletAppConfig;
import com.imseam.chatlet.config.Param;
import com.imseam.chatlet.exception.IdentifierNotExistingException;
import com.imseam.chatlet.listener.event.IEvent;
import com.imseam.cluster.IClusterCache;
import com.imseam.common.util.StringUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IMessengerWindow;
import com.imseam.raptor.cluster.invocation.EventInvocationWrapper;

public class ApplicationContext extends AbstractContext implements IApplication {
	
	private static Log log = LogFactory.getLog(ApplicationContext.class);
	
	private Map<String, IMessengerWindow> windowMap = new ConcurrentHashMap<String, IMessengerWindow>();
	
//	private Map<String, IMessengerConnection> connectionMap = new ConcurrentHashMap<String, IMessengerConnection>();
	
	private IChatletApplication chatletApplication;
	
	private ChatletAppConfig appConfig;
	
	private String resourceBundleBaseName = null;
	
	private ResourceBundle defaultResourceBundle = null;
	
	private Map<Locale, ResourceBundle> resourceBundleMap = new ConcurrentHashMap<Locale, ResourceBundle>();
	
	private Map<String, String> initParamMap = new HashMap<String, String>();
	
	private IClusterCache clusterStorage = null;
	
//	private String applicationRootPath;
	
	public ApplicationContext(ChatletAppConfig appConfig, IChatletApplication chatletApplication){
		super(true);
		this.appConfig = appConfig;
		this.chatletApplication = chatletApplication;
		
		if(appConfig.getResourceBundle() != null){
			resourceBundleBaseName = appConfig.getResourceBundle().getBaseName(); 
			defaultResourceBundle = ResourceBundle.getBundle(resourceBundleBaseName, Locale.getDefault());
		}		
		
		if(appConfig.getInitParams() != null && appConfig.getInitParams().getParam() != null){
			for(Param param : appConfig.getInitParams().getParam()){
				initParamMap.put(param.getName(), param.getValue());
			}
		}
		
		assert(!StringUtil.isNullOrEmptyAfterTrim(appConfig.getApplicationName()));
//		assert(!StringUtil.isNullOrEmptyAfterTrim(applicationRootPath));
		log.debug(String.format("Applicaiton (%s) context is created!", appConfig.getApplicationName()));
	}

	public String getApplicationName() {
		return appConfig.getApplicationName();
	}
	
	public String getApplicationRootPath(){
		return null;
	}

	@Override
	public URL getResourceURL(String resource) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IChatletApplication getApplication(){
		return chatletApplication;
	}

	
	public IWindow getWindow(String windowId) {
		return getMessengerWindow(windowId).getWindowContext();
	}

	public IMessengerWindow getMessengerWindow(String windowId){
		return this.windowMap.get(windowId); 
	}
	
	public void onWindowCreated(IMessengerWindow window) {
		windowMap.put(window.getWindowContext().getUid(), window);
	}
	
	public void onWindowStopped(IMessengerWindow window) {
		windowMap.remove(window.getWindowContext().getUid());
	}

	@Override
	public String getUid() {
		return appConfig.getApplicationName();
	}

	@Override
	public UidType getUidType() {
		return UidType.APPICATION;
	}

	@Override
	public void fireSystemEventToWinodw(String windowUid, IEvent event, IEventErrorCallback handler, String sourceUid, UidType sourceUidType) throws IdentifierNotExistingException {
		EventInvocationWrapper  invocation = new EventInvocationWrapper(event, windowUid, sourceUid, sourceUidType, new Date());
		this.getApplication().getClusterInvocationDistributor().distributeWindowRequest(handler, invocation, windowUid);
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		if(locale == null) return this.defaultResourceBundle;
		ResourceBundle bundle = this.resourceBundleMap.get(locale);
		
		if(bundle == null){
			bundle = ResourceBundle.getBundle(resourceBundleBaseName, locale);
			if(bundle == null) bundle = this.defaultResourceBundle;
			resourceBundleMap.put(locale, bundle);
		}
		
		
		return bundle;
	}

	@Override
	public String getInitParam(String paramName){
		return this.initParamMap.get(paramName);
	}

	@Override
	public IClusterCache getClusterCache() {
		return clusterStorage;
	}
	
	public void setClusterStorage(IClusterCache clusterStorage){
		this.clusterStorage = clusterStorage;
	}


	
	
}
