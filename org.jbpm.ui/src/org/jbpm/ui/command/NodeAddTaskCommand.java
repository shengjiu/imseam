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
import org.jbpm.ui.model.StartState;
import org.jbpm.ui.model.Task;
import org.jbpm.ui.model.TaskNode;

public class NodeAddTaskCommand extends Command {
	
	private Node target;
	private Task task;
	
	public void execute() {
		if (task == null) {
			createTask();
		}
		doAddTask();
	}
	
	public void undo() {
		doRemoveTask();
	}
	
	public void setTarget(Node newTarget) {
		target = newTarget;
	}
	
	private void createTask() {
		IDOMNode node = (IDOMNode)target.getNode().getOwnerDocument().createElement("task");
		task = (Task)ElementAdapterFactory.INSTANCE.adapt(node);
		task.setName(doGetNextTaskName());
	}
	
	private void doAddTask() {
		if (target instanceof StartState) {
			((StartState)target).addTask(task);
		} else {
			((TaskNode)target).addTask(task);
		}
	}
	
	private void doRemoveTask() {
		if (target instanceof StartState) {
			((StartState)target).remove(task);
		} else {
			((TaskNode)target).removeTask(task);
		}
	}
	
	private String doGetNextTaskName() {
		if (target instanceof StartState) {
			return "task";
		} else {
			return ((TaskNode)target).getNextTaskName();
		}
	}
	
}
