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

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.jbpm.ui.figure.NodeFigure;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.util.LabelCellEditorLocator;
import org.jbpm.ui.util.NodeLabelDirectEditManager;

public class NodeGraphicalEditPart 
	extends ElementGraphicalEditPart 
	implements NodeEditPart {
	
	private DirectEditManager manager;
	
	public NodeGraphicalEditPart(Node node) {
		super(node);
	}
	
	public Node getNode() {
		return (Node)getModel();
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return getNodeFigure().getLeavingConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return getNodeFigure().getArrivingConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return getNodeFigure().getLeavingConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return getNodeFigure().getArrivingConnectionAnchor();
	}

	protected List getModelSourceConnections() {
		return getNode().getLeavingTransitions();
	}
	
	protected List getModelTargetConnections() {
		return getNode().getArrivingTransitions();
	}
	
	public void refreshVisuals() {
		getNodeFigure().setName(getNode().getName());
		Rectangle constraint = null;
		if (getNode().getConstraint() != null) {
			constraint = new Rectangle(getNode().getConstraint());
		} else {
			constraint = new Rectangle(new Point(0, 0), new Dimension(-1, -1));			
		}
		Dimension initialSize = getNode().getInitialSize();
		Dimension preferredSize = getNodeFigure().getPreferredSize();
		if (constraint.width == -1 && initialSize.width > preferredSize.width) {
			constraint.width = initialSize.width;
		}
		if (constraint.height == -1 && initialSize.height > preferredSize.height) {
			constraint.height = initialSize.height;
		}
		((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), constraint);
	}
	
	public void update(Observable o, Object arg) {
		int messageId = ((Integer)arg).intValue();
		switch(messageId) {
			case NODE_ARRIVING_TRANSITION_ADDED:
			case NODE_ARRIVING_TRANSITION_REMOVED:
				refreshTargetConnections();
				break;
			case NODE_LEAVING_TRANSITION_ADDED:
			case NODE_LEAVING_TRANSITION_REMOVED:
				refreshSourceConnections();
				break;
			default:
				refresh();
				break;
		}		
	}

	private NodeFigure getNodeFigure() {
		return (NodeFigure)getFigure();
	}

	private void performDirectEdit() {
		if (getNodeFigure().getNameLabel() == null) return;
		if (manager == null) {
			initializeManager();
		}
		manager.show();
	}
	
	private void initializeManager() {
		LabelCellEditorLocator locator = new LabelCellEditorLocator(getNodeFigure().getNameLabel());
		manager = new NodeLabelDirectEditManager(this, TextCellEditor.class, locator);
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			performDirectEdit();
		} else {
			super.performRequest(request);
		}
	}

}
