/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jbpm.ui.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.Transition;


public class NodeDeleteCommand extends GraphElementDeleteCommand {

	private Node node;
	private HashMap transitionSources;

	public void setNode(Node node) {
		this.node = node;
		super.setTarget(node);
	}
	
	private void detachTransitions(List list) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				detachTransition((Transition)list.get(i));				
			}
		}
	}
	
	private void reattachTransitions() {
		if (transitionSources != null) {
			Iterator iterator = transitionSources.keySet().iterator();
			while (iterator.hasNext()) {
				reattachTransition((Transition)iterator.next());
			}
		}
	}
	
	private void detachTransition(Transition transition) {
		transitionSources.put(transition, transition.getSource());
		transition.getSource().removeLeavingTransition(transition);
	}
	
	private void reattachTransition(Transition transition) {
		Node source = (Node)transitionSources.get(transition);
		source.addLeavingTransition(transition);
	}
	
	public void execute() {		
		transitionSources = new HashMap();
		detachTransitions(node.getLeavingTransitions());
		detachTransitions(node.getArrivingTransitions());
		super.execute();
	}
	
	public void undo() {
		super.undo();
		reattachTransitions();
	}
	
	
	
}
