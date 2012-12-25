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
package org.jbpm.ui.part.graph;

import java.util.List;
import java.util.Observable;

import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToHelper;
import org.jbpm.ui.model.ProcessDefinition;

public class ProcessDefinitionGraphicalEditPart 
extends ElementGraphicalEditPart { 
	
	public ProcessDefinitionGraphicalEditPart(ProcessDefinition processDefinition) {
		super(processDefinition);
	}
	
	protected List getModelChildren() {
		return getProcessDefinition().getNodes();
	}
	
	private ProcessDefinition getProcessDefinition() {
		return (ProcessDefinition)getModel();
	}
	
	public void update(Observable o, Object arg) {
		int messageId = ((Integer)arg).intValue();
		switch (messageId) {
			case ELEMENT_NAME_SET:
				refreshVisuals(); 
				break;
			case PROCESS_DEFINITION_NODE_ADDED:
			case PROCESS_DEFINITION_NODE_REMOVED:
				refreshChildren(); 
				break;
			default: 
		}
	}
	
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			return constructSnapToHelper();
		}
		return super.getAdapter(adapter);
	}

	private Object constructSnapToHelper() {
		Boolean val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
		if (val != null && val.booleanValue()) {
			return new SnapToGrid(this);
		} else {
			return null;
		}		
	}

	
}
