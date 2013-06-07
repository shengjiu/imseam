package com.imseam.raptor.standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IApplication;
import com.imseam.chatlet.IChatlet;
import com.imseam.chatlet.IChatletFilter;
import com.imseam.chatlet.config.ChatletAppConfig;
import com.imseam.chatlet.config.ConnectionConfig;
import com.imseam.chatlet.config.ListenerConfig;
import com.imseam.chatlet.config.Param;
import com.imseam.chatlet.config.util.ConfigUtil;
import com.imseam.chatlet.listener.IMeetingEventListener;
import com.imseam.chatlet.listener.ISystemEventListener;
import com.imseam.chatlet.listener.event.ApplicationEvent;
import com.imseam.chatlet.listener.event.ConnectionEvent;
import com.imseam.common.util.ClassUtil;
import com.imseam.common.util.ExceptionUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IEventListenerManager;
import com.imseam.raptor.IMessengerConnection;
import com.imseam.raptor.IRequestProcessor;
import com.imseam.raptor.ISessionManager;
import com.imseam.raptor.IWindowManager;
import com.imseam.raptor.chatlet.ApplicationContext;
import com.imseam.raptor.chatlet.EventTypeEnum;
import com.imseam.raptor.cluster.IClusterInvocationDistributor;
import com.imseam.raptor.cluster.IClusterStorage;
import com.imseam.raptor.cluster.IMeetingStorage;
import com.imseam.raptor.core.ConnectorManager;
import com.imseam.raptor.core.EventListenerManager;
import com.imseam.raptor.core.FilterManager;

public class Application implements IChatletApplication {
	
	private static Log log = LogFactory.getLog(Application.class);
	
 	private Map<String, IMessengerConnection> connectionMap = new HashMap<String, IMessengerConnection>();
    
	private IEventListenerManager eventListenerManager = null;
	
	private UserSessionManager sessionManager = null;
	
	private RequestProcessor requestProcessor = null;
	
//	private final static String REQUEST_PROCESSOR ="USER_REQUEST_PROCESSOR";

//	private StandardChatletDispatcher chatletDispatcher = new StandardChatletDispatcher();
	
	private ApplicationContext applicationContext = null;
	
	private IMeetingEventListener meetingEventListener = null;
	
	private IClusterInvocationDistributor syncClusterRequestDistributor = null;
	
	private IWindowManager windowManager = null;
	
	private final static String REQUEST_TASK_DISTRIBUTOR ="REQUEST_TASK_DISTRIBUTOR";
	

	private List<IChatletFilter> filterList = new ArrayList<IChatletFilter>();
	
	private ChatletAppConfig appConfig;
	
	private IChatlet chatlet;
	
//	private final static String CLUSTER_EVENT_MANAGER ="CLUSTER_EVENT_MANAGER";

	private IClusterStorage clusterStorage;
	
	private final static String CLUSTER_STORAGE ="CLUSTER_STORAGE";

	private IMeetingStorage meetingStorage;
	
	private final static String MEETING_STORAGE = "MEETING_STORAGE";
	
	
	public Application(){
		
	}
	
	public void initialize(ChatletAppConfig appConfig) {
		
		log.info("The chatlet standard application start initializing.");
		log.info("Chatlet application Name: " + appConfig.getApplicationName());

		this.applicationContext = new ApplicationContext(appConfig, this);
		
		this.appConfig = appConfig;
		
		eventListenerManager = new EventListenerManager();
		eventListenerManager.initApplication(this);

		windowManager = new WindowManager();
		windowManager.initApplication(this);
		
		requestProcessor =  new RequestProcessor();
		requestProcessor.initApplication(this);
		
		sessionManager = new UserSessionManager();
		sessionManager.initApplication(this);
		
		processConfig();
		
		ApplicationEvent appEvent = new ApplicationEvent(this, applicationContext);
		eventListenerManager.fireEvent(EventTypeEnum.ApplicationInitialized, appEvent);
		
		log.info("The chatlet standard application finished initialization.");
	}
	
	
	private void processConfig() {
		log.info("Processing chat applicaiton config.");

		if (appConfig.getInitParams() != null) {
			for (Param param : appConfig.getInitParams().getParam()) {
				applicationContext.setAttribute(param.getName(), param.getValue());

				if(param.getName().equals(MEETING_STORAGE)){
					this.meetingStorage = (IMeetingStorage) ClassUtil.createInstance(param.getValue());
					meetingStorage.initApplication(this);
				}
				if(param.getName().equals(CLUSTER_STORAGE)){
					this.clusterStorage =(IClusterStorage) ClassUtil.createInstance(param.getValue());
					clusterStorage.initApplication(this);
				}
				if(param.getName().equals(REQUEST_TASK_DISTRIBUTOR)){
					this.syncClusterRequestDistributor = (IClusterInvocationDistributor) ClassUtil.createInstance(param.getValue());
					syncClusterRequestDistributor.initApplication(this);
				}
			}
		}
		
		if(meetingStorage == null){
			meetingStorage = new MeetingStorage();
			meetingStorage.initApplication(this);
		}
		
		if(clusterStorage == null){
			clusterStorage = new ClusterStorage();
			clusterStorage.initApplication(this);
		}
		
		if(syncClusterRequestDistributor == null){
			syncClusterRequestDistributor = new LocalClusterRequestDistributor();
			syncClusterRequestDistributor.initApplication(this);
		}

		if (appConfig.getFilterMappings() != null) {
			for (String filterName : appConfig.getFilterMappings().getFilterName()) {
				IChatletFilter filter = FilterManager.instance().getFilter(filterName);
				if(filter == null){
					ExceptionUtil.createRuntimeException(String.format("The filter (%s) referenced in the application %s, is not defined", filterName, this.getApplicationName()));
				}
				this.filterList.add(filter);

			}
		}
		if (appConfig.getConnections() == null) {
			ExceptionUtil.createRuntimeException("The application has to contain at least one connection definition.");
		}

		for (ConnectionConfig connectionConfig : appConfig.getConnections().getConnection()) {
			IMessengerConnection connection = ConnectorManager.instance().createConnection(this, connectionConfig);
			connectionMap.put(connection.getUid(), connection);
		}

		if (appConfig.getListeners() != null) {
			for (ListenerConfig listenerConfig : appConfig.getListeners().getListener()) {
				ISystemEventListener listener = (ISystemEventListener) ClassUtil.createInstance(listenerConfig.getListenerClass());
				listener.initialize(this, ConfigUtil.convertParams(listenerConfig.getInitParams()));
				eventListenerManager.addListner(listener);
			}
		}

		if (appConfig.getChatlet() != null) {
			chatlet = (IChatlet) ClassUtil.createInstance(appConfig.getChatlet().getChatletClass());
			chatlet.initialize(this, ConfigUtil.convertParams(appConfig.getChatlet().getInitParams()));
		}

		if (appConfig.getMeetingEventListener() != null) {
			if(appConfig.getMeetingEventListener().getMeetingEventListenerClass().equalsIgnoreCase(appConfig.getChatlet().getChatletClass())){
				meetingEventListener = (IMeetingEventListener)chatlet;
			}else{
				meetingEventListener = (IMeetingEventListener) ClassUtil.createInstance(appConfig.getMeetingEventListener().getMeetingEventListenerClass());
				meetingEventListener.initialize(this, ConfigUtil.convertParams(appConfig.getMeetingEventListener().getInitParams()));
			}
		}
	}
	
	public IMessengerConnection getConnection(String connectionUid){
		return this.connectionMap.get(connectionUid);
	}
	
	public IChatlet getChatlet() {
		return chatlet;
	}

	public String getApplicationName(){
		return this.applicationContext.getApplicationName();
	}

	@Override
	public void startApplication() {
		log.info(String.format("Starting applicaiton(%s)", this.getApplicationName()));
		
		
		for(IMessengerConnection connection : connectionMap.values()){
			connection.connect();
		}
		log.info(String.format("Applicaiton(%s) started", this.getApplicationName()));
		
		this.sessionManager.start();

	}
	
	@Override
	public void stopApplication() {
		log.info(String.format("Stopping applicaiton(%s)", this.getApplicationName()));
		for(IMessengerConnection connection : connectionMap.values()){
			connection.disconnect();
			ConnectionEvent event = new ConnectionEvent(this, connection.getConnectionContext());
			eventListenerManager.fireEvent(EventTypeEnum.ConnectionStopped, event);
		}
		ApplicationEvent appEvent = new ApplicationEvent(this, applicationContext);
		try{
			eventListenerManager.fireEvent(EventTypeEnum.ApplicationStopped, appEvent);
		}catch(Exception exp){
			exp.printStackTrace();
		}
		log.info(String.format("Applicaiton(%s) stopped", this.getApplicationName()));
		
	}

	public List<IChatletFilter> getFilterList() {
		return filterList;
	}

	public IApplication getApplicationContext() {
		assert(applicationContext != null);
		return this.applicationContext;
	}

	public IRequestProcessor getRequestProcessor() {
		assert(requestProcessor != null);
		return this.requestProcessor;
	}


	public ISessionManager getSessionManager() {
		assert(this.sessionManager != null);
		return sessionManager;
	}

//	public IChatletDispatcher getChatletDispatcher() {
//		assert(this.chatletDispatcher != null);
//		return this.chatletDispatcher;
//	}

	public IEventListenerManager getEventListenerManager(){
		assert(this.eventListenerManager != null);
		return this.eventListenerManager;
	}

	
	@Override
	public IClusterInvocationDistributor getClusterInvocationDistributor() {
		return this.syncClusterRequestDistributor;
	}

	@Override
	public IClusterStorage getClusterStorage() {
		return this.clusterStorage;
	}

	@Override
	public IMeetingStorage getMeetingStorage() {
		return this.meetingStorage;
	}

	@Override
	public IWindowManager getWindowManager() {
		return this.windowManager;
	}


	@Override
	public IMeetingEventListener getMeetingEventListener() {
		return meetingEventListener;
	}

}
