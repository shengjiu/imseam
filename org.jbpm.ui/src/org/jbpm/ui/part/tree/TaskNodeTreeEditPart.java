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
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.Task;
import org.jbpm.ui.model.TaskNode;

public class TaskNodeTreeEditPart extends NodeTreeEditPart {
	
	public TaskNodeTreeEditPart(Node node) {
		super(node);
	}
	
	public Node getNode() {
		return (Node)getModel();
	}

	protected List getModelChildren() {
		List result = new ArrayList();
		result.addAll(((TaskNode)getNode()).getTasks());
		result.addAll(getNode().getEvents());
		result.addAll(getNode().getLeavingTransitions());
		return result;
	}

	protected void refreshVisuals(){
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(
				SharedImages.class, "icon/node_obj.gif");
		setWidgetImage(SharedImages.INSTANCE.getImage(descriptor));	
		String name = getNode().getName();
		setWidgetText(name == null ? "task" : name);
	}

	private List collectTasksFromModel() {
		return new ArrayList(((TaskNode)getNode()).getTasks());
	}
	
	private List collectTasksFromTreePart() {
		ArrayList result = new ArrayList();
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof TaskTreeEditPart) {
				result.add(((TaskTreeEditPart)getChildren().get(i)).getModel());
			}
		}
		return result;
	}
	
	private void handleTaskAdded() {
		List tasksFromModel = collectTasksFromModel();
		List tasksFromTree = collectTasksFromTreePart();
		if (tasksFromModel.size() > tasksFromTree.size()) {
			tasksFromModel.removeAll(tasksFromTree);
			Task task = (Task)tasksFromModel.get(0);
			refreshChildren();
			getViewer().select((EditPart)getViewer().getEditPartRegistry().get(task));
		}
	}
	
	public void update(Observable o, Object arg) {
		int messageId = ((Integer)arg).intValue();
		switch(messageId) {
			case ELEMENT_TASK_ADDED:
				handleTaskAdded();
				break;
			default:
				super.update(o, arg);
				break;
		}		
	}

}
