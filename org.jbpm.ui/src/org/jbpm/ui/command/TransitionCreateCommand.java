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

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.factory.ElementAdapterFactory;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.Transition;

public class TransitionCreateCommand extends Command {
	
	private Node source;
	private Node target;
	private Transition transition;
	
	public boolean canExecute() {
		if (source == null || target == null) {
			return false;
		} else {
			return true;
		}
	}
	
	private void createTransition() {
		IDOMNode node = (IDOMNode)source.getNode().getOwnerDocument().createElement("transition");
		transition = (Transition)ElementAdapterFactory.INSTANCE.adapt(node);
		node = (IDOMNode)transition.getNode().getOwnerDocument().createAttribute("name");
		node.setNodeValue(source.getNextTransitionName());
		transition.getNode().getAttributes().setNamedItem(node);
		node = (IDOMNode)transition.getNode().getOwnerDocument().createAttribute("to");
		node.setNodeValue(target.getName());
		transition.getNode().getAttributes().setNamedItem(node);
	}
	
	public void execute() {
		if (transition == null) {
			createTransition();
		}
		source.addLeavingTransition(transition);
	}
	
	public void undo() {
		source.removeLeavingTransition(transition);
	}
	
	public void setSource(Node newSource) {
		source = newSource;
	}
	
	public Node getSource() {
		return source;
	}
	
	public void setTarget(Node newTarget) {
		target = newTarget;
	}
	
}
