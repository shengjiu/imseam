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
package org.jbpm.ui.model;

import java.util.Observable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IActionFilter;

public abstract class Element extends Observable implements IAdaptable, IActionFilter {
	
	public abstract ElementType getElementType();
	
	protected void notifyChange(int messageId) {
		setChanged();
		notifyObservers(new Integer(messageId));
	}
	
	protected void notifyChange() {
		setChanged();
		notifyObservers();
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		if (target != this) return false;
		if ("nodeTagEqualsTo".equals(name)) {
			return value.equals(((Element)target).getElementType().getName());
		} else if ("nodeTagDifferentFrom".equals(name)) {
			return !value.equals(((Element)target).getElementType().getName());
		} else {
			return false;
		}
	}
	
}
