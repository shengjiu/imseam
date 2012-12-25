package com.imseam.chatpage.pageflow;

import org.dom4j.Element;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.SubProcessResolver;

public class ChatflowSubProcessResolver implements SubProcessResolver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7514051031211732304L;

	public ProcessDefinition findSubProcess(Element subProcessElement) {
		
		String subProcessName = subProcessElement.attributeValue("name");

		return JbpmManager.getInstance().getChatflowProcessDefinition(subProcessName);
		
	}

	

}
