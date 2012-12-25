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
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.model.Action;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.util.HandlerConfigurationHelper;

public class NodeActionPropertyPage extends PropertyPage implements PluginConstants {
	
	Button actionCheckBox;
	HandlerConfigurationComposite handlerConfigurationComposite;
	
	public NodeActionPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}
	
	private Node getNode() {
		return (Node)getElement();
	}

	protected Control createContents(Composite parent) {
		createActionCheckBox(parent);
		createSeparator(parent);
		createHandlerConfigurationComposite(parent);
		setTitle("Node Action");
		return parent;
	}
	
	private Map assembleInitialActionHandlerConfigurationValues() {
		Map initialValues = new HashMap();
		Action action = getNode().getAction();
		if (action != null) {
			initialValues.put("class", action.getDelegateClassName());
			String type = HandlerConfigurationHelper.configStringFor(action.getConfigurationType());
			if (type != null) {
				initialValues.put("type", type);
				initialValues.put(type, action.getConfigurationInfo());
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
				assembleInitialActionHandlerConfigurationValues());
		handlerConfigurationComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		handlerConfigurationComposite.showDetails(actionCheckBox.getSelection());
	}
		
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.horizontalSpan = 3;
		separatorData.heightHint = 10;
		separator.setLayoutData(separatorData);
	}
	
	private void createActionCheckBox(final Composite parent) {
		actionCheckBox = new Button(parent, SWT.CHECK);
		GridData assignmentCheckBoxData = new GridData();
		assignmentCheckBoxData.horizontalSpan = 3;
		actionCheckBox.setLayoutData(assignmentCheckBoxData);
		actionCheckBox.setText("Configure action handler");
		actionCheckBox.setSelection(getNode().hasAction());
		actionCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlerConfigurationComposite.showDetails(((Button)e.widget).getSelection());
			}
		});
	}
	
    public boolean performOk() {
		Node node = getNode();
		if (actionCheckBox.getSelection()) {
			if (!node.hasAction()) node.addAction();
			Action action = node.getAction();
			action.setDelegateClassName(getAssignmentDelegateClassName());
			action.setConfigurationType(
					HandlerConfigurationHelper.configXMLFor(getAssignmentConfigurationType()));
			addActionConfigurationInfo(action, getAssignmentConfigurationData());
		} else {
			node.removeAction();
		}
		return true;
	}
    
    private String getAssignmentDelegateClassName() {
    	return handlerConfigurationComposite.getClassName();
    }
    
    private String getAssignmentConfigurationType() {
    	String result = handlerConfigurationComposite.getConfigType();
    	return FIELD_CONFIG_TYPE.equals(result) ? null : result;
    }
    
    private Object getAssignmentConfigurationData() {
    	return handlerConfigurationComposite.getConfigData();
    }
	
	private void addActionConfigurationInfo(Action action, Object info) {
		if (info instanceof TreeMap) {
			action.addConfigurationInfo((TreeMap)info);
		} else {
			action.addConfigurationInfo((String)info);			
		}
	}
	
}
