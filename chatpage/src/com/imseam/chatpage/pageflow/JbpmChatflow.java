package com.imseam.chatpage.pageflow;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.ProcessState;

import com.imseam.chatlet.IAttributes;
import com.imseam.chatlet.IMessageSender;
import com.imseam.chatlet.IUserRequest;
import com.imseam.chatlet.IWindow;
import com.imseam.chatpage.ChatPageManager;
import com.imseam.chatpage.IChatPage;
import com.imseam.common.util.StringUtil;

//Window scoped
public class JbpmChatflow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4907119935600291265L;

	private static final Log log = LogFactory.getLog(JbpmChatflow.class);

	private int counter;

	private ProcessInstance processInstance;
	
	private String chatflowDefinitionName = null;

	public String getChatflowDefinitionName() {
		return chatflowDefinitionName;
	}

	public boolean isInProcess() {
		return processInstance != null;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public int getChatflowCounter() {
		return counter;
	}

	public Node getNode() {
		if (processInstance == null)
			return null;
		return getNode(processInstance);
	}

	public ChatPageNode getChatPageNode() {
		return (ChatPageNode) getNode();
	}

	private Node getNode(ProcessInstance subProcessInstance) {
		if (subProcessInstance == null)
			return null;
		Token chatflowToken = subProcessInstance.getRootToken();
		Node node = chatflowToken.getNode();
		if (node == null) {
			throw new IllegalStateException("chatflow has not yet started");
		}
		return node;
	}

	private ChatPageNode getChatPageNode(ProcessInstance subProcessInstance) {
		Node node = getNode(subProcessInstance);
		if (!(node instanceof ChatPageNode)) {
			throw new IllegalStateException(
					"chatflow is not currently at a <page> or <start-page> node (note that chatflows that begin during the RENDER_RESPONSE phase should use <start-page> instead of <start-state>)");
		}
		return (ChatPageNode) node;
	}

	private ProcessInstance getCurrentProcessIntance(ProcessInstance subProcessInstance) {
		Node node = getNode(subProcessInstance);
		if (node instanceof ProcessState) {
			if (subProcessInstance.getRootToken().getSubProcessInstance() == null) {
				throw new IllegalStateException("chatflow has not yet started");
			}
			subProcessInstance = getCurrentProcessIntance(subProcessInstance.getRootToken().getSubProcessInstance());
		}
		return subProcessInstance;
	}

	public void reposition(String nodeName) {
		if (processInstance == null) {
			throw new IllegalStateException("no chatflow in progress");
		}
		Node node = processInstance.getProcessDefinition().getNode(nodeName);
		if (node == null) {
			throw new IllegalArgumentException("no node named: " + nodeName + " for chatflow: " + processInstance.getProcessDefinition().getName());
		}
		processInstance.getRootToken().setNode(node);
	}

	public boolean hasTransition(String outcome) {
		ProcessInstance currentProcessInstance = this.getCurrentProcessIntance(processInstance);

		Node node = getNode(currentProcessInstance);

		return hasTransition(node, outcome);
	}

	public boolean hasTransition(Node node, String outcome) {

		return StringUtil.isNullOrEmptyAfterTrim(outcome) ? node.getDefaultLeavingTransition() != null : node.getLeavingTransition(outcome) != null;
	}

	public void navigate(IAttributes request, IMessageSender responseSender, String outcome) {
		assert (!StringUtil.isNullOrEmptyAfterTrim(outcome));

		ProcessInstance currentProcessInstance = this.getCurrentProcessIntance(processInstance);

		Node node = getNode(currentProcessInstance);

		navigate(null, request, responseSender, outcome, currentProcessInstance, node);

	}

	private void navigate(String input, IAttributes request, IMessageSender responseSender, String outcome, ProcessInstance currentProcessInstance, Node node) {

		if (hasTransition(node, outcome)) {
			// trigger the named transition
			log.debug("Find transition, signal it:" + outcome);

			ChatflowVariableUtil.setInput(currentProcessInstance.getContextInstance(), input);
			ChatflowVariableUtil.setRequest(currentProcessInstance.getContextInstance(), request);
			ChatflowVariableUtil.setResponse(currentProcessInstance.getContextInstance(), responseSender);

//			System.out.println("request: " + request);

			signal(currentProcessInstance, outcome);
		} else {
			log.debug("No transition found: " + outcome);
		}

		if (processInstance.hasEnded()) {
			// Events.instance().raiseEvent(
			// "com.imseam.seam.endchatflow."
			// + processInstance.getProcessDefinition().getName());
		}

	}

	public boolean processChatInput(final String input, IUserRequest request, IMessageSender responseSender) {
		assert (!StringUtil.isNullOrEmptyAfterTrim(input));

		try{
//			if(!request.getRequestFromChannel().getWindow().getUid().equalsIgnoreCase(this.beginWindowId)){
//				System.out.println("request from Window: " + request.getRequestFromChannel().getWindow().getUid() + ", beginWindowId: " + beginWindowId);
//			}
		if (!isInProcess()) {
			log.warn("Process chat input request not in a chatflow process");
			return false;
		}

		final ProcessInstance currentProcessInstance = this.getCurrentProcessIntance(processInstance);

		final ChatPageNode chatPageNode = getChatPageNode(currentProcessInstance);

		final IChatPage chatPage = ChatPageManager.getInstance().getChatPage(chatPageNode.getFullPathViewId());

		String outcome = chatPage.parseAndProcessInput(input, request);
		

		if (!StringUtil.isNullOrEmptyAfterTrim(outcome)) {

			navigate(input, request, responseSender, outcome, currentProcessInstance, chatPageNode);

		} else {
			log.debug("No outcome parsed out for input: " + input);
			// raiseEventToBackBean(chatPageNode,
			// BuildInEventEnum.UNPARSABLE_INPUT, input);
		}
		
//		Method getTextMessageMethod;
//		String responseText = null;
//		try {
//			getTextMessageMethod = responseSender.getClass().getMethod("getTextMessage");
//			responseText = (String) getTextMessageMethod.invoke(responseSender);
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		String requestText = request.getRequestContent().toString(); 
//		if((!requestText.equalsIgnoreCase(responseText)) || StringUtil.isNullOrEmptyAfterTrim(responseText))
//		{
//			System.out.println("request: " + requestText +", response: " + responseText +", outcome:" + outcome + ", chatpageNode: " + chatPage.getViewID() + ", currentProcessInstance:" + currentProcessInstance);
//		}
		}catch(Exception exp){
			exp.printStackTrace();
		}
		
		return true;
	}

	// private void raiseEventToBackBean(ChatPageNode chatPageNode, String
	// event, Object... params) {
	// if (!StringUtil.isNullOrEmptyAfterTrim(chatPageNode.getBackBean())) {
	// chatPageNode.raiseEventToBackBean(BuildInEventEnum.UNPARSABLE_INPUT,
	// params);
	// }
	// }

	public boolean signalTransition(String transition) {
		assert (!StringUtil.isNullOrEmptyAfterTrim(transition));

		ProcessInstance currentProcessInstance = this.getCurrentProcessIntance(processInstance);
		ChatPageNode chatPageNode = getChatPageNode(currentProcessInstance);

		if (!isInProcess() || !hasTransition(chatPageNode, transition)) {
			log.debug(String.format("The conversation is NOT in a chatflow process(isInProcess:%s) or No transition found(hasTransition:%s), Event type: %s", isInProcess(),
					hasTransition(chatPageNode, transition), transition));
			return false;
		}

		// trigger the named transition
		log.debug("Find transition for systemevent, signal it:" + transition);
		signal(currentProcessInstance, transition);
		if (processInstance.hasEnded()) {
			// Events.instance().raiseEvent(
			// "com.imseam.seam.endchatflow."
			// + processInstance.getProcessDefinition().getName());
		}
		return true;
	}

	public void processEvents(String type) {
		Event event = getNode().getEvent(type);
		if (event != null) {
			@SuppressWarnings("unchecked")
			List<Action> actions = (List<Action>) event.getActions();
			for (Action action : actions) {
				try {
					action.execute(ExecutionContext.currentExecutionContext());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	

	public void begin(String chatflowDefinitionName, IAttributes request, IWindow window, String welcome) {

		if (log.isDebugEnabled()) {
			log.debug("beginning chatflow: " + chatflowDefinitionName);
		}
		
		this.chatflowDefinitionName = chatflowDefinitionName;

		processInstance = newChatflowInstance(JbpmManager.getInstance().getChatflowProcessDefinition(chatflowDefinitionName));

		Node node = getNode(processInstance);
//		System.out.println("begin processInstance: " + processInstance +", node: " + node.hashCode());
		
		
		if (hasTransition(node, welcome)) {
			this.navigate(null, request, window.getMessageSender(), welcome, processInstance, node);
		}
	}

	private void signal(ProcessInstance processInstance, String outcome) {
		log.debug("performing pageflow nagivation for outcome " + outcome);
		processInstance.signal(outcome);
	}

	private ProcessInstance newChatflowInstance(ProcessDefinition processDefinition) {
		log.debug("new pageflow instance for " + processDefinition.getName());
		return processDefinition.createProcessInstance();
	}

	@Override
	public String toString() {
		String processName = processInstance == null ? "null" : (processInstance.getProcessDefinition().getName() + ":" + processInstance);
		return "chatflow(" + super.toString() + ", process:" + processName + ")";
	}

}
