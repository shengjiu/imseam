package com.imseam.chatlet.config;

import java.io.ByteArrayInputStream;

import com.imseam.chatlet.config.util.ConfigReader;

public class MarshallerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EngineConfig engConfig = new EngineConfig();
		
		ChatletAppConfig appConfig = new ChatletAppConfig();
		appConfig.setApplicationName("Test chatlet app Name");
		
		ResourceBundleConfig resourceBundleConfig = new ResourceBundleConfig();
		resourceBundleConfig.setBaseName("basename1");
		appConfig.setResourceBundle(resourceBundleConfig);
		appConfig.setConnections(new Connections());
		
		
		ConnectionConfig connectionConfig = new ConnectionConfig();
		connectionConfig.setConnectionName("Shengjiu Test");
		connectionConfig.setConnectorRef("IncesoftMSN");
		connectionConfig.setHostUserID("shengjiu@hasd");
		connectionConfig.setPassword("Password1");
		connectionConfig.setServiceID("MSN");
		//connectionConfig.get
		appConfig.getConnections().getConnection().add(connectionConfig);
		
		
		ChatletConfig chatletConfig = new ChatletConfig();
		
		chatletConfig.setChatletClass("com.imseam.chat.WelcomeChatlet");
		appConfig.setChatlet(chatletConfig);
		MeetingEventListenerConfig listener = new MeetingEventListenerConfig();
		listener.setMeetingEventListenerClass("com.imseam.chat.MeetingEventListener");
		appConfig.setMeetingEventListener(listener);
		
		Listeners appListeners = new Listeners();
		ListenerConfig appListener = new ListenerConfig();
		appListener.setListenerClass("com.imseam.listener");
		appListener.setName("app.listener.name");
		
		appListeners.getListener().add(appListener);
		
		
		appConfig.setListeners(appListeners);
		
//		Chatflow chatflow = new Chatflow();
//		chatflow.setClassPath("com.imseam.chatflow.Classpath");
//		appConfig.setChatflow(chatflow);
//		
//		WelcomeChatpage welcomeChatpage = new WelcomeChatpage();
//		welcomeChatpage.setWelcomeChatpage("welcomeChatPage");
//		appConfig.setWelcomeChatpage(welcomeChatpage);
		
		engConfig.setChatletApps(new ChatletApps());
		
		engConfig.getChatletApps().getChatApp().add(appConfig);
		
		Connectors connectors = new Connectors();
		ConnectorConfig jmsnConnector = new ConnectorConfig();
		jmsnConnector.setClassName("com.imseam.msn.incesoft.IncesoftMSNConnector");
		jmsnConnector.setName("JMSN");
		connectors.getConnector().add(jmsnConnector);
		
		engConfig.setConnectors(connectors);
		
		
		String engineXML = ConfigReader.marshallEngineConfig(engConfig);
		System.out.println("Marshall EngineConfig: ");
		System.out.println(engineXML);
		
		
		
		EngineConfig engConfig2 = ConfigReader.parserEngineConfig(new ByteArrayInputStream(engineXML.getBytes()));
		System.out.println("");
		System.out.println("");
		System.out.println("Unmarshall EngineConfig: ");
		System.out.println(ConfigReader.marshallEngineConfig(engConfig2));
	}

}
