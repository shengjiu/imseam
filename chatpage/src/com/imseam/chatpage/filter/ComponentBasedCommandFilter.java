package com.imseam.chatpage.filter;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatpage.IChatPage;
import com.imseam.chatpage.IParser;
import com.imseam.chatpage.impl.ParserCarrier;
import com.imseam.common.util.StringUtil;

/*
 * the component based command filter has limitation to pass parameters list
 */
public class ComponentBasedCommandFilter extends ParserCarrier implements CommandFilter {
	
	
	private static Log log = LogFactory.getLog(ComponentBasedCommandFilter.class);
	
	private String componentName;
	
	public ComponentBasedCommandFilter(
			String componentName,
			List<IParser> parserList,
			Map<String, String> parameters
			){
		super(parserList);
		assert(!StringUtil.isNullOrEmptyAfterTrim(componentName));
		assert(parserList != null);
		assert(parameters != null);
		
//		this.componentName = componentName;
//		Component component = Seam.componentForName(componentName);
//		if(component == null){
//			ExceptionUtil.createRuntimeException("Cannot create imseam component: " + componentName);
//		}
//		Class componentClass = component.getBeanClass();
//		if(!ClassUtil.isSubclass(componentClass, ComponentBasedCommandFilter.class)){
//			ExceptionUtil.createRuntimeException("The bean class(%s) of component(%s) is not a subclass of ChatPage.class", componentName, componentClass);
//		}
		log.warn("the component based command filter has limitation to pass parameters list, and parseInput will not be called");
	}
	
	private CommandFilter getChatPageComponent(){
		return null; //(CommandFilter) Component.getInstance(componentName);
	}

	public String getComponentName() {
		return componentName;
	}

	public void doCommandFilter(IChatPage chatpage, IUserRequest request, String input, CommandFilterChain chain, IMessageSender responseSender) {
		this.getChatPageComponent().doCommandFilter(chatpage, request, input, chain, responseSender);
	}


}
