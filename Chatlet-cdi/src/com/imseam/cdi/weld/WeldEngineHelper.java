package com.imseam.cdi.weld;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.Container;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.manager.api.WeldManager;

import com.imseam.cdi.chatlet.ChatletLifecycle;
import com.imseam.chatlet.IApplication;

public class WeldEngineHelper {
	private static WeldEngineHelper instance = new WeldEngineHelper();
	private WeldManager weldManager;
	private ChatletLifecycle lifecycle;
	private static final Log log = LogFactory.getLog(WeldEngineHelper.class);


	public static WeldEngineHelper getInstance(){
		return instance;
	}
	
	public void initApplication(IApplication application){
		weldManager = (WeldManager)application.getAttribute(BeanManager.class.getName());
	}
	
	public ChatletLifecycle getLifecycle() {
		if (lifecycle == null) {
			this.lifecycle = Container.instance().services().get(ChatletLifecycle.class);
		}
		return lifecycle;
	}	
	
	public <T> T getInstanceFromWeldEngine(Class<T> type, Annotation... annotations){
		Bean<? extends Object> bean = weldManager.resolve(weldManager.getBeans(type, annotations));
		if(bean == null) return null;
		try{
			@SuppressWarnings("unchecked")
			T instance = (T) ((BeanManagerImpl)weldManager).getReference(bean, weldManager.createCreationalContext(bean), true);
			return instance;
		}catch(Exception exp){
			log.warn("The instance cannot be created by Weld engine, "+ exp.getMessage());
		}
//		T instance = (T) ((BeanManagerImpl)weldManager).getReference(bean, type, weldManager.createCreationalContext(bean), true);
		return null;
	}
	
	public WeldManager getWeldManager(){
		return weldManager;
	}


}
