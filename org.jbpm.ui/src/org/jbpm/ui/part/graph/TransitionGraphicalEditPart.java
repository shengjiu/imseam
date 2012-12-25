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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.jbpm.ui.figure.TransitionFigure;
import org.jbpm.ui.model.Bendpoint;
import org.jbpm.ui.model.Element;
import org.jbpm.ui.model.GraphElement;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.NotificationMessages;
import org.jbpm.ui.model.Transition;
import org.jbpm.ui.part.GraphElementEditPart;
import org.jbpm.ui.policy.EditPolicyInstaller;
import org.jbpm.ui.util.EditPartSelectionProviderAdapter;

public class TransitionGraphicalEditPart 
	extends AbstractConnectionEditPart
	implements NotificationMessages, Observer, GraphElementEditPart {
	
	public TransitionGraphicalEditPart(Transition transition) {
		setModel(transition);
	}
	
	private Transition getTransition() {
		return (Transition)getModel();
	}

	protected IFigure createFigure() {
		GraphElement element = (GraphElement)getModel();
		TransitionFigure result = (TransitionFigure)element.getElementType().getContributor().createFigure();
		result.setRoutingConstraint(constructFigureBendpointList(result));
		return result;
	}
	
	private List constructFigureBendpointList(TransitionFigure f) {
		ArrayList result = new ArrayList();
		List modelBendpoints = getTransition().getBendpoints();
		for (int i = 0; i < modelBendpoints.size(); i++) {
			Bendpoint bendpoint = (Bendpoint)modelBendpoints.get(i);
			RelativeBendpoint figureBendpoint = new RelativeBendpoint(f);
			figureBendpoint.setRelativeDimensions(
					bendpoint.getFirstRelativeDimension(), 
					bendpoint.getSecondRelativeDimension());
			figureBendpoint.setWeight((i + 1) / (modelBendpoints.size() + 1));
			result.add(figureBendpoint);
		}
		return result;
	}
	
	private void refreshBendpoints() {
		TransitionFigure f = (TransitionFigure)getFigure();
		f.setRoutingConstraint(constructFigureBendpointList(f));
	}
	
	protected void createEditPolicies() {
		EditPolicyInstaller.installEditPolicies(this);
	}

	public void activate() {
		if (!isActive()) {
			getTransition().addObserver(this);
			super.activate();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			getTransition().deleteObserver(this);
			super.deactivate();
		}
	}
	
	protected List getModelChildren() {
		ArrayList result = new ArrayList();
		result.add(getTransition().getLabel());
		return result;
	}

	private void refreshTarget() {
		Node targetNode = ((Transition)getModel()).getTarget();
		if (targetNode != null) {
			EditPart targetPart = 
				(EditPart)getRoot().getViewer().getEditPartRegistry().get(targetNode);
			if (targetPart != null) {
				setTarget(targetPart);
			}
		} else {
			setTarget(null);
		}
		refreshTargetAnchor();
		refreshVisuals();
	}
			
	public void update(Observable o, Object arg) {
		int messageId = ((Integer)arg).intValue();
		switch(messageId) {
			case TRANSITION_BENDPOINT_ADDED:
			case TRANSITION_BENDPOINT_MOVED:
			case TRANSITION_BENDPOINT_REMOVED:
				refreshBendpoints();
			case ELEMENT_NAME_SET:
				refresh();
				break;
			case TRANSITION_TARGET_CHANGED:
				refreshTarget();
			default:
				break;
		}		
	}
	
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_OPEN) {
			openPropertyDialog();
		} else {
			super.performRequest(request);
		}
	}
	
	private void openPropertyDialog() {
		IShellProvider shellProvider = new SameShellProvider(getViewer().getControl().getShell());
		PropertyDialogAction propertyDialogAction = new PropertyDialogAction(shellProvider, new EditPartSelectionProviderAdapter(getViewer()));
		propertyDialogAction.run();		
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		Element element = (Element)((TransitionGraphicalEditPart)target).getModel();
		return element.testAttribute(element, name, value);
	}
	
	
}
