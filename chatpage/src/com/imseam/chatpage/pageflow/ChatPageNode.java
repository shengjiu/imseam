package com.imseam.chatpage.pageflow;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatpage.ChatPageManager;

public class ChatPageNode extends Node implements Parsable {

	
//	private static Log log = LogFactory.getLog(ChatPageNode.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -920667583245016069L;

	// This classname is configured in the jbpm configuration 
	// file : org/jbpm/graph/node/node.types.xml inside 
	// the jbpm-{version}.jar

	// In case it would be necessary, that file, can be customized
	// by updating the reference to it in the central jbpm configuration 
	// file 'jbpm.cfg.xml'

	private String viewId;

	private boolean isChatEnd = false;

	private String description;
	
	private String backBean;
	
	//milliseconds
	private int timeout = 0;
	
	private Map<String, String> otherAttributeMap = new HashMap<String, String>();
	
	/**
	 * parses the dom4j element that corresponds to this page.
	 */
	@Override
	public void read(Element pageElement, JpdlXmlReader jpdlXmlReader) {
		
		for(Object attributeObj: pageElement.attributes()){
			
			Attribute attribute = (Attribute) attributeObj;
			String attributeName = attribute.getName();
			String attributeValue = attribute.getValue();
			
			if(attributeName.equalsIgnoreCase("view-id")){
				viewId = attributeValue;
				continue;
			}
			
			if(attributeName.equalsIgnoreCase("back-bean")){
				backBean = attributeValue;
				continue;
			}
			
			otherAttributeMap.put(attributeName, attributeValue);
		}
		
		if(pageElement.attributeValue("timeout") != null){
			timeout = Integer.parseInt(pageElement.attributeValue("timeout"));
		}
		
		Element endChatElement = pageElement.element("end-chat");
		if (endChatElement != null) {
			isChatEnd = true;

		}

		Element descriptionElement = pageElement.element("description");
		if (descriptionElement != null) {
			description = descriptionElement.getTextTrim();
		}
	}



	/**
	 * is executed when execution arrives in this page at runtime.
	 */
	@Override
	public void execute(ExecutionContext executionContext) {
		

		ContextInstance processContext = executionContext.getContextInstance();
		String input = ChatflowVariableUtil.getInput(processContext);
		IAttributes request = ChatflowVariableUtil.getRequest(processContext);
		IMessageSender response = ChatflowVariableUtil.getResponse(processContext);
		
		ChatPageManager.getInstance().renderPageBody(viewId, input, request, response);
		afterRenderSaveState();
	}
	
	protected void afterRenderSaveState(){
		ChatPageManager.getInstance().afterRenderSaveState();
	}
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isChatEnd() {
		return isChatEnd;
	}

	public void setChatEnd(boolean isChatEnd) {
		this.isChatEnd = isChatEnd;
	}

	public String getFullPathViewId() {
		return viewId;
	}

	public void setFullPathViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getBackBean() {
		return backBean;
	}

	public void setBackBean(String backBean) {
		this.backBean = backBean;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public String getAttribute(String attributeName){
		return this.otherAttributeMap.get(attributeName);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public synchronized Map getLeavingTransitionsMap() {
	    return super.getLeavingTransitionsMap();
	}


}
