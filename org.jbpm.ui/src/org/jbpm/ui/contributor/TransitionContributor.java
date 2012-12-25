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
package org.jbpm.ui.contributor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.jbpm.ui.figure.TransitionFigure;
import org.jbpm.ui.model.Element;
import org.jbpm.ui.model.Transition;
import org.jbpm.ui.part.graph.TransitionGraphicalEditPart;
import org.jbpm.ui.part.tree.TransitionTreeEditPart;

public class TransitionContributor implements ElementContributor {

	public Object createElement() {
		return new Transition();
	}

	public EditPart createGraphicalEditPart(Element element) {
		return new TransitionGraphicalEditPart((Transition)element);
	}

	public EditPart createTreeEditPart(Element element) {
		return new TransitionTreeEditPart((Transition)element);
	}

	public IFigure createFigure() {
		return new TransitionFigure();
	}
}
