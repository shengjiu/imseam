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

import java.util.Observer;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.jbpm.ui.model.Element;
import org.jbpm.ui.model.GraphElement;
import org.jbpm.ui.model.NotificationMessages;
import org.jbpm.ui.part.GraphElementEditPart;
import org.jbpm.ui.policy.EditPolicyInstaller;
import org.jbpm.ui.util.EditPartSelectionProviderAdapter;

public abstract class ElementTreeEditPart extends AbstractTreeEditPart implements
		Observer, NotificationMessages, Listener, GraphElementEditPart {
	
	public ElementTreeEditPart(GraphElement graphElement) {
		super(graphElement);
	}
	
	private GraphElement getGraphElement() {
		return (GraphElement)getModel();
	}

	public void activate() {
		if (!isActive()) {
			getGraphElement().addObserver(this);
			getWidget().addListener(SWT.DefaultSelection, this);
			super.activate();
		}
	}
	
	public void handleEvent(Event event) {
		openPropertyDialog();
	}
	
	public void deactivate() {
		if (isActive()) {
			getGraphElement().deleteObserver(this);
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
	
	protected void createEditPolicies() {
		EditPolicyInstaller.installEditPolicies(this);
	}

	public Object getAdapter(Class adapter) {
		if (IWorkbenchAdapter.class.equals(adapter)) {
			return new IWorkbenchAdapter() {
				public Object[] getChildren(Object o) {
					return null;
				}
				public ImageDescriptor getImageDescriptor(Object object) {
					return null;
				}
				public String getLabel(Object o) {
					return ((ElementTreeEditPart)o).getGraphElement().getName();
				}
				public Object getParent(Object o) {
					return null;
				}				
			};
		}
		return super.getAdapter(adapter);
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		Element element = (Element)((ElementTreeEditPart)target).getModel();
		return element.testAttribute(element, name, value);
	}
	

	
}
