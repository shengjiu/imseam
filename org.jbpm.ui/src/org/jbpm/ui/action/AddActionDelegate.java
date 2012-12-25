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
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.jbpm.ui.command.GraphElementAddActionCommand;
import org.jbpm.ui.editor.DesignerEditor;
import org.jbpm.ui.model.Event;
import org.jbpm.ui.model.EventTypes;
import org.jbpm.ui.model.GraphElement;
import org.jbpm.ui.model.Transition;
import org.jbpm.ui.outline.DesignerOutlineViewer;
import org.jbpm.ui.part.tree.EventTreeEditPart;

public class AddActionDelegate implements IObjectActionDelegate {
	
	private IWorkbenchPart targetPart;
	private EditPart selectedPart;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		CommandStack commandStack;
		String eventType = getEventType(action.getId());
		if (eventType == null) return;
		if (targetPart instanceof ContentOutline) {
			commandStack = ((DesignerOutlineViewer)((ContentOutline)targetPart).getCurrentPage()).getCommandStack();
		} else if (targetPart instanceof DesignerEditor) {
			commandStack = ((DesignerEditor)targetPart).getCommandStack();
		} else return;
		GraphElementAddActionCommand command = new GraphElementAddActionCommand();
		command.setTarget(getTargetElement());
		command.setEventType(eventType);
		commandStack.execute(command);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection == null) return;
		if (!(selection instanceof StructuredSelection)) return;
		Object object = ((StructuredSelection)selection).getFirstElement();
		if (object instanceof EditPart) {
			selectedPart = (EditPart)object;
		}
	}
	
	private GraphElement getTargetElement() {
		if (selectedPart instanceof EventTreeEditPart) {
			return (GraphElement)selectedPart.getParent().getModel();
		} else {
			return (GraphElement)selectedPart.getModel();
		}
	}
		
	private GraphElement getSelectedElement() {
		return (GraphElement)selectedPart.getModel();
		
	}

	private String getEventType(String actionId) {
		if ("processStart".equals(actionId)) return EventTypes.EVENTTYPE_PROCESS_START;
		if ("processEnd".equals(actionId)) return EventTypes.EVENTTYPE_PROCESS_END;
		if ("beforeSignal".equals(actionId)) return EventTypes.EVENTTYPE_BEFORE_SIGNAL;
		if ("afterSignal".equals(actionId)) return EventTypes.EVENTTYPE_AFTER_SIGNAL;
		if ("nodeEnter".equals(actionId)) return EventTypes.EVENTTYPE_NODE_ENTER;
		if ("nodeLeave".equals(actionId)) return EventTypes.EVENTTYPE_NODE_LEAVE;
		if ("addAction".equals(actionId) && getSelectedElement() instanceof Transition) return EventTypes.EVENTTYPE_TRANSITION;
		return ((Event)getSelectedElement()).getType();
	}
	
}
