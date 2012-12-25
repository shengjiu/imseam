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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.jbpm.ui.DesignerLogger;

public class EditPolicyDescriptor {
	
	private static Map policyMap;
	
	private String id;
	private String role;
	private String partClassName;
	private String policyClassName;
	private IConfigurationElement creator;
  
	public static EditPolicyDescriptor[] getPoliciesForEditPart(String editPartClassName) {
		if (policyMap == null) {
			initializePolicyMap();
		}
		List list = (List)policyMap.get(editPartClassName);
		if (list != null) {
			return (EditPolicyDescriptor[])list.toArray(new EditPolicyDescriptor[list.size()]);
		} else {
			return new EditPolicyDescriptor[0];
		}
	}
	
	private static void initializePolicyMap() {
		policyMap = new HashMap();
		IExtension[] extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.jbpm.ui.editPolicies").getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) {
				processConfigElement(configElements[j]);
			}
		}				
	}
	
	private static void processConfigElement(IConfigurationElement configElement) {
		if (!configElement.getName().equals("editPolicy")) return;
		EditPolicyDescriptor descriptor = new EditPolicyDescriptor();
		descriptor.id = configElement.getAttribute("id");
		descriptor.role = configElement.getAttribute("role");
		descriptor.partClassName = configElement.getAttribute("part");
		descriptor.policyClassName = configElement.getAttribute("policy");
                descriptor.creator = configElement;
		addDescriptorToMap(descriptor);
	}
	
	private static void addDescriptorToMap(EditPolicyDescriptor descriptor) {
		List list = (List)policyMap.get(descriptor.partClassName);
		if (list == null) {
			list = new ArrayList();
			policyMap.put(descriptor.partClassName, list);
		}
		list.add(descriptor);
	}

	public String getId() {
		return id;
	}

	public String getPartClassName() {
		return partClassName;
	}

	public String getPolicyClassName() {
		return policyClassName;
	}

	public String getRole() {
		return role;
	}
	
	public AbstractEditPolicy createEditPolicy() {
		AbstractEditPolicy result = null;
		try {
			result = (AbstractEditPolicy)creator.createExecutableExtension("policy");
		}
    catch(CoreException e)
    {
      DesignerLogger.logError("Problem while trying to instantiate editpolicy : " + getPolicyClassName(), e);
    }

    return result;
	}
	
}
