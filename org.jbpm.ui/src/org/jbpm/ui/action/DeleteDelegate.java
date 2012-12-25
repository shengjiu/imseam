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
package org.jbpm.ui.action;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.jbpm.ui.command.GraphElementDeleteCommand;
import org.jbpm.ui.command.NodeDeleteCommand;
import org.jbpm.ui.model.GraphElement;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.outline.DesignerOutlineViewer;

public class DeleteDelegate implements IObjectActionDelegate {
	
	private IWorkbenchPart targetPart;
	private EditPart selectedPart;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		CommandStack commandStack = ((DesignerOutlineViewer)((ContentOutline)targetPart).getCurrentPage()).getCommandStack();
		Command command = createDeleteCommand((GraphElement)selectedPart.getModel());
		commandStack.execute(command);
	}
	
	private Command createDeleteCommand(GraphElement element) {
		if (element instanceof Node) {
			NodeDeleteCommand result = new NodeDeleteCommand();
			result.setNode((Node)element);
			return result;
		} else {
			GraphElementDeleteCommand result = new GraphElementDeleteCommand();
			result.setTarget(element);
			return result;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection == null) return;
		if (!(selection instanceof StructuredSelection)) return;
		Object object = ((StructuredSelection)selection).getFirstElement();
		if (object instanceof EditPart) {
			selectedPart = (EditPart)object;
		}
	}
	
}
