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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.jbpm.ui.command.TransitionLabelMoveCommand;
import org.jbpm.ui.model.TransitionLabel;
import org.jbpm.ui.part.graph.TransitionGraphicalEditPart;

public class TransitionLabelGraphicalNodeEditPolicy extends NonResizableEditPolicy {
	
	public Command getMoveCommand(ChangeBoundsRequest request) {
	  TransitionLabel model = (TransitionLabel)getHost().getModel();
	  Point delta = request.getMoveDelta();
	  TransitionLabelMoveCommand command = new TransitionLabelMoveCommand(model,getParentFigure(),delta);
	  return command; 
	}
	
	public Figure getParentFigure() {
	  TransitionGraphicalEditPart transitionGraphicalEditPart = 
		  (TransitionGraphicalEditPart)getHost().getParent();
	  return (Figure)transitionGraphicalEditPart.getFigure();
	}
}