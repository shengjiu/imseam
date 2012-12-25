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
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jbpm.ui.SharedImages;
import org.jbpm.ui.model.Action;
import org.jbpm.ui.model.Event;

public class EventTreeEditPart extends ElementTreeEditPart {
	
	public EventTreeEditPart(Event event) {
		super(event);
	}
	
	private Event getEvent() {
		return (Event)getModel();
	}
	
	protected List getModelChildren() {
		Collection coll = getEvent().getActions();
		return coll == null ? new ArrayList() : new ArrayList(coll);
	}

	protected void refreshVisuals(){
		ImageDescriptor descriptor = ImageDescriptor.createFromFile(
				SharedImages.class, "icon/event_obj.gif");
		setWidgetImage(SharedImages.INSTANCE.getImage(descriptor));		
		setWidgetText(getEvent().getType());
	}

	private List collectActionsFromModel() {
		return new ArrayList(getEvent().getActions());
	}
	
	private List collectActionsFromTreePart() {
		ArrayList result = new ArrayList();
		for (int i = 0; i < getChildren().size(); i++) {
			EditPart currentChild = (EditPart)getChildren().get(i);
			result.add(currentChild.getModel());
		}
		return result;
	}
		
	private void handleEventActionAdded() {
		List modelActions = collectActionsFromModel();
		List treeActions = collectActionsFromTreePart();
		if (modelActions.size() > treeActions.size()) {
			modelActions.removeAll(treeActions);
			EditPart newPart = new ActionTreeEditPart((Action)modelActions.get(0));
			addChild(newPart, getChildren().size());
			getViewer().select(newPart);
		}
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_OPEN) {
			return;
		} 
		super.performRequest(request);
	}
	
	public void update(Observable o, Object arg) {
		int messageId = ((Integer)arg).intValue();
		switch(messageId) {
		case ELEMENT_ACTION_ADDED:
			handleEventActionAdded();
			break;
		case ELEMENT_ACTION_REMOVED:
			refreshChildren();
			break;
		case ELEMENT_NAME_SET:
			refreshVisuals();
		default:
			break;
		}		
	}
}
