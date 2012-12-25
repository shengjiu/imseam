package com.imseam.chatpage.pageflow;

import org.jbpm.graph.exe.ExecutionContext;

public class NoWaitChatPageNode extends ChatPageNode{

	/**
	 * 
	 */
	

	// This classname is configured in the jbpm configuration 
	// file : org/jbpm/graph/node/node.types.xml inside 
	// the jbpm-{version}.jar

	// In case it would be necessary, that file, can be customized
	// by updating the reference to it in the central jbpm configuration 
	// file 'jbpm.cfg.xml'


	/**
	 * 
	 */
	private static final long serialVersionUID = -8137263128651640325L;

	/**
	 * is executed when execution arrives in this page at runtime.
	 */
	@Override
	public void execute(ExecutionContext executionContext) {
		super.execute(executionContext);
		this.leave(executionContext);
	}
	
	@Override
	protected void afterRenderSaveState(){
		
	}

}
