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
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.jbpm.ui.figure.TransitionFigure;
import org.jbpm.ui.model.Transition;
import org.jbpm.ui.model.TransitionLabel;
import org.jbpm.ui.util.LabelCellEditorLocator;
import org.jbpm.ui.util.TransitionLabelDirectEditManager;

public class TransitionLabelGraphicalEditPart 
	extends ElementGraphicalEditPart implements Observer {
	
	private DirectEditManager manager;
	
	public TransitionLabelGraphicalEditPart(TransitionLabel transitionLabel) {
		super(transitionLabel);
	}
	
	public Transition getTransition() {
		return (Transition)getParent().getModel();
	}
	
	private TransitionLabel getTransitionLabel() {
		return (TransitionLabel)getModel();
	}
	
	private String getTransitionLabelText() {
		String result = getTransition().getName();
		return result == null ? "" : result;
	}

	protected IFigure createFigure() {
		return new Label(getTransitionLabelText());
	}

	protected void refreshVisuals() {
		String text = getTransitionLabelText();
		TransitionGraphicalEditPart parent = (TransitionGraphicalEditPart)getParent(); 									                      
		TransitionFigure transitionFigure = (TransitionFigure)parent.getFigure();
		Point offset = getTransitionLabel().getOffset();
		if (offset == null) {
			offset = calculateInitialOffset(transitionFigure);
			getTransitionLabel().setOffset(offset);
		}
		Label figure = (Label)getFigure();
		figure.setText(text);
		TransitionLabelConstraint constraint = 
			new TransitionLabelConstraint(text, offset, transitionFigure);
		parent.setLayoutConstraint(this,getFigure(),constraint);	  
	}
	
	private Point calculateInitialOffset(TransitionFigure transitionFigure) {
		Point result = new Point(5, -10);
		Point start = transitionFigure.getStart();
		Point end = transitionFigure.getEnd();
		Point mid = start.getNegated().getTranslated(end).getScaled(0.5);
		if (mid.x < -10) {
			result.y = 10;
		}
		return result;
	}
		  
	public DragTracker getDragTracker(Request request) {
		return new TransitionLabelTracker(this,(TransitionGraphicalEditPart)getParent()); 	
	}
		  
	private void performDirectEdit() {
		if (manager == null) {
			initializeManager();
		}
		manager.show();
	}
	
	private void initializeManager() {
		LabelCellEditorLocator locator = new LabelCellEditorLocator((Label)getFigure());
		manager = new TransitionLabelDirectEditManager(this, TextCellEditor.class, locator);
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
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

	public void update(Observable arg0, Object arg1) {
		refreshVisuals();		
	}
	
}
