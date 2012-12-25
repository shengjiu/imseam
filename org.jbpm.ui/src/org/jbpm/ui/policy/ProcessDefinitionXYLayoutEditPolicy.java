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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.jbpm.ui.command.NodeChangeConstraintCommand;
import org.jbpm.ui.command.NodeCreateCommand;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.ProcessDefinition;

public class ProcessDefinitionXYLayoutEditPolicy extends XYLayoutEditPolicy {

	protected Command createAddCommand(EditPart arg0, Object arg1) {
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		NodeChangeConstraintCommand locationCommand = new NodeChangeConstraintCommand();
		locationCommand.setNode((Node)child.getModel());
		locationCommand.setNewConstraint((Rectangle)constraint);
		return locationCommand;
	}

	protected Command getCreateCommand(CreateRequest request) {
		NodeCreateCommand createCommand = new NodeCreateCommand();
		createCommand.setNode((Node)request.getNewObject());
		createCommand.setParent((ProcessDefinition)getHost().getModel());
		createCommand.setLocation(request.getLocation());
		createCommand.setLabel("create a node");
		return createCommand;
	}

	protected Command getDeleteDependantCommand(Request arg0) {
		return null;
	}
	
	protected EditPolicy createchildEditPolicy(EditPart child) {
		return new NonResizableEditPolicy();
	}
	
}
