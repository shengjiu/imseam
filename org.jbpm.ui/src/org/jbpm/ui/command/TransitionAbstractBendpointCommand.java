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
package org.jbpm.ui.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.jbpm.ui.model.Bendpoint;
import org.jbpm.ui.model.Transition;

public abstract class TransitionAbstractBendpointCommand extends Command {
	
	protected Transition transition;
	protected int index;
	protected Bendpoint bendpoint;
	
	public void setTransitionDecorator(Transition transition) {
		this.transition = transition;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
		bendpoint = new Bendpoint();
		bendpoint.setRelativeDimensions(dim1, dim2);
	}
	
}
