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
package org.jbpm.ui.model;

import java.util.List;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NodeList;



public class StartState extends Node  {

	public boolean isPossibleChildOf(ProcessDefinition candidateParent) {
		if (!getProcessDefinition().equals(candidateParent)) return false;
		List nodes = getProcessDefinition().getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof StartState) return false;
		}
		return true;
	}
	
	public String getNamePrefix() {
		return "start";
	}

	public void add(GraphElement adapter) {
		if (adapter instanceof Task) {
			notifyChange(ELEMENT_TASK_ADDED);
		} 
		super.add(adapter);
	}
	
	public void remove(GraphElement adapter) {
		if (adapter instanceof Task) {
			notifyChange(ELEMENT_TASK_REMOVED);
		}
		super.remove(adapter);
	}
	
	protected GraphElement getTaskBeforeInsertionPoint() {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter candidate = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Event || candidate instanceof Transition) {
				return (GraphElement)candidate;
			}
		}
		return null;
	}
	
	protected GraphElement getTaskAfterInsertionPoint() {
		NodeList list = getNode().getChildNodes();
		for (int i = list.getLength(); i > 0; i--) {
			INodeAdapter candidate = ((IDOMNode)list.item(i - 1)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Task) {
				return (GraphElement)candidate;
			}
		}
		return null;
	}
	
	public void addTask(Task task) {
		GraphElement first = getTaskBeforeInsertionPoint();
		IDOMNode before = null;
		if (first == null) {
			GraphElement last = getTaskAfterInsertionPoint();
			if (last == null) {
				before = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
				getNode().appendChild(before);
			} else {
				before = (IDOMNode)last.getNode().getNextSibling();				
			}
		} else {
			before = (IDOMNode)first.getNode().getPreviousSibling();
		}
		addTaskBefore(task, before);
	}
	
	public void addTaskBefore(Task task, IDOMNode before) {
		getNode().insertBefore(task.getNode(), before);
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, task.getNode());
		add(task);
	}
	
	public Task getTask() {
		NodeList nodeList = getNode().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)nodeList.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Task) {
				return (Task)adapter;
			}
		}
		return null;
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		if (target != this) return false;
		if ("canAddTasks".equals(name)) {
			if ("true".equals(value)) {
				return getTask() == null;
			} else {
				return false;
			}
		}
		return super.testAttribute(target, name, value);
	}
}
