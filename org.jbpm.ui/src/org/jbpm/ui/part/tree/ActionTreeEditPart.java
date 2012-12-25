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

import java.util.Observable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.jbpm.ui.SharedImages;
import org.jbpm.ui.model.Action;

public class ActionTreeEditPart extends ElementTreeEditPart {
	
	public ActionTreeEditPart(Action action) {
		super(action);
	}
	
	private Action getAction() {
		return (Action)getModel();
	}
	
	protected void refreshVisuals(){
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(
				SharedImages.class, "icon/action_obj.gif");
		setWidgetImage(SharedImages.INSTANCE.getImage(descriptor));		
		setWidgetText(getActionName());
	}
	
	private String getActionName() {
		String result = getAction().getName();
		return result == null ? "action" : result;
	}

	public void update(Observable o, Object arg) {
		int messageId = ((Integer)arg).intValue();
		switch(messageId) {
			case ELEMENT_ACTION_ADDED:
			case ELEMENT_ACTION_REMOVED:
				refreshChildren();
				break;
			case ELEMENT_NAME_SET:
				refreshVisuals();
			default:
				break;
		}		
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
					return ((ActionTreeEditPart)o).getAction().getName();
				}
				public Object getParent(Object o) {
					return null;
				}				
			};
		}
		return super.getAdapter(adapter);
	}
}

