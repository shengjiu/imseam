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

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.jbpm.ui.model.Element;
import org.jbpm.ui.model.ProcessDefinition;
import org.jbpm.ui.part.graph.ProcessDefinitionGraphicalEditPart;
import org.jbpm.ui.part.tree.ProcessDefinitionTreeEditPart;

public class ProcessDefinitionContributor implements ElementContributor {

	public Object createElement() {
		return new ProcessDefinition();
	}

	public EditPart createGraphicalEditPart(Element element) {
		return new ProcessDefinitionGraphicalEditPart((ProcessDefinition)element);
	}

	public EditPart createTreeEditPart(Element element) {
		return new ProcessDefinitionTreeEditPart((ProcessDefinition)element);
	}

	public IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		layer.setBorder(new LineBorder(1));
		return layer;
	}
}
