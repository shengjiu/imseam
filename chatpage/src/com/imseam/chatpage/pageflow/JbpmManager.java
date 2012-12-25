package com.imseam.chatpage.pageflow;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.util.ClassLoaderUtil;
import org.xml.sax.InputSource;

//Application scoped
public class JbpmManager {
	private static Log log = LogFactory.getLog(JbpmManager.class);
	private static JbpmManager instance = new JbpmManager();

	private JbpmConfiguration chatflowConfiguration = null;
	
	private Map<String, String> definitionResourceMap = new ConcurrentHashMap<String, String>();

	private JbpmManager() {
		chatflowConfiguration = JbpmConfiguration.parseResource("com/imseam/chatpage/pageflow/jbpm.chatflow.cfg.xml");
	}

	public static JbpmManager getInstance() {
		return instance;
	}

	private String[] chatflowDefinitions;
	private Map<String, ProcessDefinition> chatflowProcessDefinitions = new HashMap<String, ProcessDefinition>();

	public void startup(String... chatflowDefinitions) throws Exception {
		log.trace("Starting jBPM Chatflow manager");
		this.chatflowDefinitions = chatflowDefinitions;
		if (chatflowDefinitions != null) {
			for (String chatflow : chatflowDefinitions) {
				ProcessDefinition pd = getChatflowDefinitionFromResource(chatflow);
				chatflowProcessDefinitions.put(pd.getName(), pd);
				definitionResourceMap.put(pd.getName(), chatflow);
			}
		}

		JbpmExpressionEvaluator.setVariableResolver(new PageflowVariableResolver());

	}

	public ProcessDefinition getChatflowProcessDefinition(String chatflowName) {
		//return chatflowProcessDefinitions.get(chatflowName);
		String resource = this.definitionResourceMap.get(chatflowName);
		return getChatflowDefinitionFromResource(resource);
	}

	public ProcessDefinition getChatflowDefinitionFromResource(String resourceName) {
		InputStream resource = ClassLoaderUtil.getStream(resourceName);
		if (resource == null) {
			throw new IllegalArgumentException("chatflow resource not found: " + resourceName);
		}
		return parseInputSource(new InputSource(resource));
	}

	public JbpmContext createPageflowContext() {
		return chatflowConfiguration.createJbpmContext();
	}

	private ProcessDefinition parseXmlString(String xml) {
		StringReader stringReader = new StringReader(xml);
		return parseInputSource(new InputSource(stringReader));
	}

//	private ProcessDefinition parseXmlResource(String xmlResource) {
//		InputStream resourceStream = ClassLoaderUtil.getStream(xmlResource);
//		return parseInputSource(new InputSource(resourceStream));
//	}

	private ProcessDefinition parseInputSource(InputSource inputSource) {
		JbpmContext jbpmContext = createPageflowContext();
		try {
			ChatflowParser pageflowParser = new ChatflowParser(inputSource);
			return pageflowParser.readProcessDefinition();
		} finally {
			jbpmContext.close();
		}
	}

	public String[] getChatflowDefinitions() {
		return chatflowDefinitions;
	}

	public void setChatflowDefinitions(String[] chatflowDefinitions) {
		this.chatflowDefinitions = chatflowDefinitions;
	}

	/**
	 * Dynamically deploy a page flow definition, if a pageflow with an
	 * identical name already exists, the pageflow is updated.
	 * 
	 * @return true if the pageflow definition has been updated
	 */
	public boolean deployChatflowDefinition(ProcessDefinition chatflowDefinition) {
		return chatflowProcessDefinitions.put(chatflowDefinition.getName(), chatflowDefinition) != null;
	}

	/**
	 * Read a pageflow definition
	 * 
	 * @param pageflowDefinition
	 *            the pageflow as an XML string
	 */
	public ProcessDefinition getChatflowDefinitionFromXml(String chatflowDefinition) {
		return parseXmlString(chatflowDefinition);
	}

	/**
	 * Remove a pageflow definition
	 * 
	 * @param pageflowName
	 *            Name of the pageflow to remove
	 * @return true if the pageflow definition has been removed
	 */
	public boolean undeployChatflowDefinition(String chatflowName) {
		return chatflowProcessDefinitions.remove(chatflowName) != null;
	}

}
