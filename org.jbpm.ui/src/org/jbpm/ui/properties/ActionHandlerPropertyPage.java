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
package org.jbpm.ui.properties;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.model.Action;
import org.jbpm.ui.util.HandlerConfigurationHelper;

public class ActionHandlerPropertyPage extends PropertyPage implements PluginConstants {
	
	HandlerConfigurationComposite handlerConfigurationComposite;
	
	public ActionHandlerPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}
	
	private Action getAction() {
		return (Action)getElement();
	}

	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		createHandlerConfigurationComposite(parent);
		createSeparator(parent);
		setTitle("Action Handler");
		return parent;
	}
	
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.heightHint = 20;
		separatorData.horizontalSpan = 2;
		separator.setLayoutData(separatorData);
	}

	private Map assembleInitialActionConfigurationValues() {
		Map initialValues = new HashMap();
		String className = getAction().getDelegateClassName();
		if (className != null) {
			initialValues.put("class", className);
			String type = HandlerConfigurationHelper.configStringFor(getAction().getConfigurationType());
			if (type != null) {
				initialValues.put("type", type);
				initialValues.put(type, getAction().getConfigurationInfo());
			}
		}
		return initialValues;
	}
	
	private void createHandlerConfigurationComposite(Composite parent) {
		handlerConfigurationComposite = new HandlerConfigurationComposite(
				parent,
				"org.jbpm.graph.def.ActionHandler",
				"Choose Action Handler",
				"Choose an action handler from the list",
				assembleInitialActionConfigurationValues());
		GridData handlerConfigurationCompositeData = new GridData(GridData.FILL_BOTH);
		handlerConfigurationCompositeData.horizontalSpan = 2;
		handlerConfigurationComposite.setLayoutData(handlerConfigurationCompositeData);
		handlerConfigurationComposite.showDetails(true);
	}
		
    public boolean performOk() {
		getAction().setDelegateClassName(handlerConfigurationComposite.getClassName());
		getAction().setConfigurationType(getActionConfigurationType());
		getAction().addConfigurationInfo(handlerConfigurationComposite.getConfigData());
		return true;
	}
    
     private String getActionConfigurationType() {
    	String result = handlerConfigurationComposite.getConfigType();
    	result = FIELD_CONFIG_TYPE.equals(result) ? null : result;
    	return HandlerConfigurationHelper.configXMLFor(result);
    }
    
}
