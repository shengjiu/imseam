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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.ProcessDefinition;
import org.jbpm.ui.model.StartState;

public class NodeCreateCommand extends Command {
	
	private Node node;
	private Point location;
	protected ProcessDefinition parent;
	
	public void execute() {
		setNodeConstraint();
		setNodeName();
		parent.addNode(node);
	}
	
	public boolean canExecute() {
		return node.isPossibleChildOf(parent);
	}
	
	private void setNodeName() {
		if (node.getName() == null) {
			if (node instanceof StartState) {
				node.setName(node.getNamePrefix());
			} else {
				node.setName(parent.getNextNodeName(node));
			}
		}
	}
	
	private void setNodeConstraint() {
		if (location != null) {
			node.setConstraint(new Rectangle(location, new Dimension(-1, -1)));
		}		
	}
	
	public void undo() {
		parent.removeNode(node);
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public void setParent(ProcessDefinition parent) {
		this.parent = parent;
	}
	

}
