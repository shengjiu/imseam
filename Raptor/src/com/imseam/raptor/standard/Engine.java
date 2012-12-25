package com.imseam.raptor.standard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IChatletFilter;
import com.imseam.chatlet.config.ChatletAppConfig;
import com.imseam.chatlet.config.ChatletApps;
import com.imseam.chatlet.config.ConnectorConfig;
import com.imseam.chatlet.config.Connectors;
import com.imseam.chatlet.config.EngineConfig;
import com.imseam.chatlet.config.FilterConfig;
import com.imseam.chatlet.config.Filters;
import com.imseam.chatlet.config.util.ConfigUtil;
import com.imseam.common.util.ClassUtil;
import com.imseam.raptor.IChatletApplication;
import com.imseam.raptor.IChatletEngine;
import com.imseam.raptor.IMessengerConnector;
import com.imseam.raptor.core.ApplicationManager;
import com.imseam.raptor.core.ConnectorManager;
import com.imseam.raptor.core.FilterManager;

public class Engine implements IChatletEngine {
	
	private static Log log = LogFactory.getLog(Engine.class);
	
    /**
     * Descriptive information about this Server implementation.
     */
    private static final String info =
        "com.imseam.raptor.core.StandardEngine/1.0";
	
    
    /**
     * Has this component been started?
     */
//    private boolean started = false;


    /**
     * Has this component been initialized?
     */
//    private boolean initialized = false;
    
    
//    private boolean stopAwait = false;
    
    
	public Engine(){
		
	}


    /**
     * Return descriptive information about this Server implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }


	public void await() {
		// TODO Auto-generated method stub
		
	}

	public void initialize(EngineConfig engineConfig) {
		
		log.info("The chatlet standard engine start initializing.");
		Connectors connectorConfigs = engineConfig.getConnectors();
		ChatletApps appContextConfigs = engineConfig.getChatletApps();
		Filters filters = engineConfig.getFilters();
		
		for (ConnectorConfig connectorConfig :  connectorConfigs.getConnector()) {
			IMessengerConnector connector = (IMessengerConnector) ClassUtil.createInstance(connectorConfig.getClassName());
	        ConnectorManager.instance().addConnector(connectorConfig.getName(), connector);
	    }
		
		if( filters != null){
			for (FilterConfig filterConfig :  filters.getFilter()) {
				IChatletFilter filter = (IChatletFilter) ClassUtil.createInstance(filterConfig.getFilterClass());
				filter.initialize(this, ConfigUtil.convertParams(filterConfig.getInitParams()));
				FilterManager.instance().addFilter(filterConfig.getName(), filter);
			}
		}
		
		for (ChatletAppConfig appConfig :  appContextConfigs.getChatApp()) {
	        IChatletApplication chatletApp = new Application();
	        chatletApp.initialize(appConfig);
	        
	        ApplicationManager.instance().addApplication(appConfig.getApplicationName(), chatletApp);
	    }
		
//		initialized = true;
		log.info("The chatlet standard engine finished initialization.");

	}

	public void start() {
		log.info("The chatlet standard engine is starting.");
		for (IChatletApplication chatletApp : ApplicationManager.instance().getAllApplications()) {
			chatletApp.startApplication();
		}
//		started = true;
		log.info("The chatlet standard engine finished starting.");
	}

	public void stop() {
		log.info("The chatlet standard engine is stopping.");
		for (IChatletApplication chatletApp : ApplicationManager.instance().getAllApplications()) {
			chatletApp.stopApplication();
		}
		ApplicationManager.instance().removeAllApplications();
		log.info("The chatlet standard engine finished stopping.");
		
	}

}
