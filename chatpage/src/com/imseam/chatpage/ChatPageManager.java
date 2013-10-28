package com.imseam.chatpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContextListener;
import javax.el.ELResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.config.util.ChatPageConfigReader;
import com.imseam.chatpage.context.ChatpageContext;
import com.imseam.common.util.DeploymentScanner;
import com.imseam.common.util.ExceptionUtil;



public class ChatPageManager {
	private static Log log = LogFactory.getLog(ChatPageManager.class);
	
	private static ChatPageManager instance = new ChatPageManager();
	
	private Map<String, String> chatPageResourceMap = new HashMap<String, String>();
	
	private Map<String, IChatPage> chatPageMap = new HashMap<String, IChatPage>();
	
	private List<ELContextListener> elContextListenerList = new ArrayList<ELContextListener>();
	
	private ChatPageManager(){
		
	}
	
	public static ChatPageManager getInstance(){
		return instance;
	}
	
	public List<ELContextListener> getElContextListeners() {
		return elContextListenerList;
	}
	
	public void addELContextListener(ELContextListener listener){
		elContextListenerList.add(listener);
	}
	
	public void addELResolver(ELResolver elResolver){
		ChatpageContext.addELResolver(elResolver);
	}
	
	public boolean removeELContextListener(ELContextListener listener){
		return elContextListenerList.remove(listener);
	}

	public IChatPage getChatPage(String fullPathViewID){
		return chatPageMap.get(fullPathViewID);
	}
	
	public IChatPage getChatPage(String parentPath, String viewId){
		return chatPageMap.get(parentPath + IChatPage.PathSeperator + viewId);
	}

	public Collection<String> getViewIDSet(){
		return Collections.unmodifiableCollection(chatPageMap.keySet());
	}

	public Collection<IChatPage> getChatPageSet(){
		return Collections.unmodifiableCollection(chatPageMap.values());
	}

	
	public void initChatPages(){
		new ChatPageScanner("META-INF/beans.xml").startScan();
		for(String chatPageResource : chatPageResourceMap.keySet()){
			ChatPageConfigReader.loadChatPagesFromConfigResource(chatPageResource, chatPageResourceMap.get(chatPageResource));
		}
	}
	
	public void addChatPage(IChatPage chatPage){
		if(chatPageMap.get(chatPage.getFullPathViewID()) != null){
			log.warn("The view id already exists: " + chatPage.getFullPathViewID() +", it might be loaded from other Jars: ");
		}
		chatPageMap.put(chatPage.getFullPathViewID(), chatPage);
		log.debug("A chatpage is added to the chatpagemanager: " + chatPage.getFullPathViewID());
		
	}
	
	public void renderPageBody(String fullPathViewId, String input, IAttributes request, IMessageSender responseSender){
		try{
			IChatPage chatPage = this.getChatPage(fullPathViewId);
			if(chatPage == null){
				log.warn("trying to render a page body which cannot be found:" + fullPathViewId);
				return;
			}
			chatPage.redenerBody(input, request, responseSender);
		}catch(ChatPageRenderException exp){
			log.error("Exceptions happened when rendering body for chatpage: " + fullPathViewId, exp);
		}
	}
	
	public void afterRenderSaveState(){
		ChatpageContext.current().saveCurrentPageDataToWindow();
	}
	
	public void renderPageHelp(String fullPathViewId, String input, IAttributes request, IMessageSender responseSender){
		try{
			IChatPage chatPage = this.getChatPage(fullPathViewId);
			if(chatPage == null){
				log.warn("trying to render a page help which cannot be found:" + fullPathViewId);
				return;
			}
			chatPage.redenerHelp(input, request, responseSender);
		}catch(ChatPageRenderException exp){
			log.error("Exceptions happened when rendering help for chatpage: " + fullPathViewId, exp);
		}
	}

	private class ChatPageScanner extends DeploymentScanner {

		ChatPageScanner(String resourceName) {
			super(resourceName);
		}

		void startScan(){
			scan();
		}

		@Override
		protected void handleItem(String name) {
			if (name.endsWith(".csp")) {
				if(chatPageResourceMap.get(name) != null){
					log.warn("The chat page config resource already existing: " + name);
				}
				String parentPath = IChatPage.RootPath;
				if(name.lastIndexOf('/')>0){
					parentPath = name.substring(0, name.lastIndexOf('/'));
					if(!parentPath.startsWith(IChatPage.RootPath)){
						parentPath = IChatPage.RootPath + parentPath; 
					}
				}
				if(!parentPath.endsWith(IChatPage.PathSeperator)){
					parentPath = parentPath + IChatPage.PathSeperator;
				}
				log.debug(String.format("Chat page config Resource (%s) found under parent(%s)", name, parentPath));
				chatPageResourceMap.put(name, parentPath);
			}
		}
	}
	
	


}
