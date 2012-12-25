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
package org.jbpm.ui.policy;

import java.util.ArrayList;

import org.eclipse.gef.editparts.AbstractEditPart;

public class EditPolicyInstaller {
	
	public static void installEditPolicies(AbstractEditPart editPart) {
		String[] classes = getContributingClasses(editPart);
		// The hierarchy is traversed from the most general class downwards.
		for (int i = classes.length; i > 0; i--) {
			installEditPolicies(editPart, EditPolicyDescriptor.getPoliciesForEditPart(classes[i - 1]));
		}
	}

	private static void installEditPolicies(AbstractEditPart editPart, EditPolicyDescriptor[] editPolicyDescriptors) {
		for (int i = 0; i < editPolicyDescriptors.length; i++) {
			editPart.installEditPolicy(editPolicyDescriptors[i].getRole(), editPolicyDescriptors[i].createEditPolicy());
		}
	}
	
	private static String[] getContributingClasses(AbstractEditPart editPart) {
		ArrayList list = new ArrayList();
		Class current = editPart.getClass();
		list.add(current.getName());
		while (current != null && current != AbstractEditPart.class) {
			current = current.getSuperclass();
			list.add(current.getName());
		}
		return (String[])list.toArray(new String[list.size()]);
	}
}
