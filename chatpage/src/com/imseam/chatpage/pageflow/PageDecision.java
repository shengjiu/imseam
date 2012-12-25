package com.imseam.chatpage.pageflow;

import java.util.Map;

import org.jbpm.graph.node.Decision;

public class PageDecision extends Decision {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2889036705546998441L;

//	private volatile int threadVisitCount =0;
	
//	@Override
//	public Transition getLeavingTransition(String transitionName) {
////		threadVisitCount++;
////		if(threadVisitCount > 1){
////			System.out.println("thread visited more than two:before");
////		}
//		Transition transition = super.getLeavingTransition(transitionName);
////		if(threadVisitCount > 1){
////			System.out.println("thread visited more than two:after");
////		}
//		
//		return transition;
//	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public synchronized Map getLeavingTransitionsMap() {
	    return super.getLeavingTransitionsMap();
	}
}
