package com.imseam.chatpage.pageflow;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;

public class NoWaitNode extends Node implements Parsable {

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

	private String noChatViewId;
	
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
			
			if(attributeName.equalsIgnoreCase("no-chat-view-id")){
				noChatViewId = attributeValue;
				continue;
			}
			
			otherAttributeMap.put(attributeName, attributeValue);
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
		//      if ( isConversationEnd && processToCreate!=null )
		//      {
		//         BusinessProcess.instance().createProcess(processToCreate);
		//      }
		//      
		//      if ( isTaskEnd ) 
		//      {
		//         BusinessProcess.instance().endTask(transition);         
		//      }
		//
		//      if (isConversationEnd || isTaskEnd ) 
		//      {
		//         Manager.instance().endConversation(false);
		//      }
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

	public String getNoChatViewId() {
		return noChatViewId;
	}

	public void setNoChatViewId(String noChatViewId) {
		this.noChatViewId = noChatViewId;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getAttribute(String attributeName){
		return this.otherAttributeMap.get(attributeName);
	}

}
