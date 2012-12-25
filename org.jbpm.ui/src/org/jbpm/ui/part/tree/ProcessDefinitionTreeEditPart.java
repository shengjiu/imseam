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
package org.jbpm.ui.part.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jbpm.ui.SharedImages;
import org.jbpm.ui.model.Event;
import org.jbpm.ui.model.ProcessDefinition;

public class ProcessDefinitionTreeEditPart 
extends ElementTreeEditPart {

	public ProcessDefinitionTreeEditPart(ProcessDefinition processDefinition) {
		super(processDefinition);
	}

	public ProcessDefinition getProcessDefinition() {
		return (ProcessDefinition) getModel();
	}

	protected List getModelChildren() {
		List result = new ArrayList();
		result.addAll(getProcessDefinition().getEvents());
		result.addAll(getProcessDefinition().getNodes());
		return result;
	}

	protected void refreshVisuals() {
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(
				SharedImages.class, "icon/process_obj.gif");
		setWidgetImage(SharedImages.INSTANCE.getImage(descriptor));
		String name = getProcessDefinition().getName();
		setWidgetText(name == null ? "process-definition" : name);
	}

	private List collectEventsFromModel() {
		return new ArrayList(getProcessDefinition().getEvents());
	}
	
	private List collectEventsFromTreePart() {
		ArrayList result = new ArrayList();
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof EventTreeEditPart) {
				result.add(((EventTreeEditPart)getChildren().get(i)).getModel());
			}
		}
		return result;
	}
	
	private void handleEventActionAdded() {
		List eventsFromModel = collectEventsFromModel();
		List eventsFromTree = collectEventsFromTreePart();
		if (eventsFromModel.size() > eventsFromTree.size()) {
			eventsFromModel.removeAll(eventsFromTree);
			Event event = (Event)eventsFromModel.get(0);
			refreshChildren();
			getViewer().select((EditPart)getViewer().getEditPartRegistry().get(event));
		}
	}
	
	
	public void update(Observable o, Object arg) {
		int messageId = ((Integer) arg).intValue();
		switch (messageId) {
		case ELEMENT_EVENT_ADDED:
		case ELEMENT_ACTION_ADDED:
			handleEventActionAdded();
			break;
		case ELEMENT_ACTION_REMOVED:
		case ELEMENT_EVENT_REMOVED:
		case PROCESS_DEFINITION_NODE_ADDED:
		case PROCESS_DEFINITION_NODE_REMOVED:
			refreshChildren();
			break;
		case ELEMENT_NAME_SET:
			refreshVisuals();
		default:
			break;
		}
	}
	
}
