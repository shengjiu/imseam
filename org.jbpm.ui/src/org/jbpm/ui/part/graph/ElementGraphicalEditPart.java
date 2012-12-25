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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.jbpm.ui.model.Element;
import org.jbpm.ui.model.NotificationMessages;
import org.jbpm.ui.part.GraphElementEditPart;
import org.jbpm.ui.policy.EditPolicyInstaller;
import org.jbpm.ui.util.EditPartSelectionProviderAdapter;

public abstract class ElementGraphicalEditPart 
	extends AbstractGraphicalEditPart 
	implements Observer, NotificationMessages, GraphElementEditPart {
	
	public ElementGraphicalEditPart(Observable observable) {
		setModel(observable);
	}
	
	protected IFigure createFigure() {
		return getElement().getElementType().getContributor().createFigure();
	}
	
	protected void createEditPolicies() {
		EditPolicyInstaller.installEditPolicies(this);
	}
	
	private Element getElement() {
		return (Element)getModel();
	}
	
	public void activate() {
		if (!isActive()) {
			getElement().addObserver(this);
			super.activate();
		}
	}
	
	public void deactivate() {
		if (isActive()) {
			getElement().deleteObserver(this);
			super.deactivate();
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
		Element element = (Element)((ElementGraphicalEditPart)target).getModel();
		return element.testAttribute(element, name, value);
	}
	
}
