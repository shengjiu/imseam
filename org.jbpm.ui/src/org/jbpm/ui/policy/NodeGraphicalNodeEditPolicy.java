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
package org.jbpm.ui.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.jbpm.ui.command.TransitionCreateCommand;
import org.jbpm.ui.model.EndState;
import org.jbpm.ui.model.Fork;
import org.jbpm.ui.model.Join;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.StartState;
import org.jbpm.ui.model.State;

public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		Node node = getNode();
		if (node instanceof StartState) {
			return null;
		} else if (node instanceof Fork 
				&& node.getArrivingTransitions().size() == 1) {
			return null;
		} else {
			TransitionCreateCommand command = (TransitionCreateCommand)request.getStartCommand();
			if (command.getSource() == node && !(node instanceof State)) {
				return null;
			} else {
				command.setTarget(node);
				return command;
			}
		}
	}
	
	protected Node getNode() {
		return (Node)getHost().getModel();		
	}
	
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Node node = getNode();
		if (node instanceof EndState) {
			return null;
		} else if (node instanceof Join 
				&& node.getLeavingTransitions().size() == 1) {
			return null;
		} else {
			TransitionCreateCommand command = new TransitionCreateCommand();
			command.setSource(getNode());
			request.setStartCommand(command);
			return command;
		}
	}

	protected Command getReconnectTargetCommand(ReconnectRequest arg0) {
		return null;
	}

	protected Command getReconnectSourceCommand(ReconnectRequest arg0) {
		return null;
	}

}
