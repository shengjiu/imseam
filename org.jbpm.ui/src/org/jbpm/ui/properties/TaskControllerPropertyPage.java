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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jbpm.ui.model.Task;
import org.jbpm.ui.util.HandlerConfigurationHelper;

public class TaskControllerPropertyPage extends PropertyPage {
	
	Button defaultControllerButton;
	Button customControllerButton;
	DefaultControllerConfigurationComposite defaultControllerComposite;
	HandlerConfigurationComposite customControllerComposite;
	
	List controllerVariables;
	
	public TaskControllerPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}

	private Task getTask() {
		return (Task)getElement();
	}

	protected Control createContents(Composite parent) {
		controllerVariables = getTask().getControllerVariables();
		parent.setLayout(new GridLayout(2, false));
		createControllerRadioButtons(parent);
		createSeparator(parent);
		updateControl(parent);
		setTitle("Form Controller");
		return parent;
	}
	
	private void createControllerRadioButtons(final Composite parent) {
		defaultControllerButton = new Button(parent, SWT.RADIO);
		defaultControllerButton.setText("Default Controller");
		defaultControllerButton.setSelection(true);
		customControllerButton = new Button(parent, SWT.RADIO);
		customControllerButton.setText("Custom Controller");
		defaultControllerButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (defaultControllerComposite != null && ((Button)e.widget).getSelection()) return;
				updateControl(parent);
			}
		});
		defaultControllerButton.setSelection(getTask().getControllerDelegateClassName() == null);
		customControllerButton.setSelection(getTask().getControllerDelegateClassName() != null);
	}
	
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.horizontalSpan = 3;
		separatorData.heightHint = 10;
		separator.setLayoutData(separatorData);
	}
	
	private void updateControl(Composite parent) {
		if (customControllerButton.getSelection()) {
			deleteDefaultControllerComposite(parent);
			createCustomControllerComposite(parent);
		} else  {
			deleteCustomControllerComposite(parent);
			createDefaultControllerComposite(parent);
		}
		parent.layout();
	}
	
	private Map assembleInitialControllerConfigurationValues() {
		Map initialValues = new HashMap();
		if (getTask().hasController()) {
			initialValues.put("class", getTask().getControllerDelegateClassName());
			String type = HandlerConfigurationHelper.configStringFor(getTask().getControllerConfigurationType());
			if (type != null) {
				initialValues.put("type", type);
				initialValues.put(type, getTask().getControllerConfigurationInfo());
			}
		}
		return initialValues;
	}
	
	private void createCustomControllerComposite(Composite parent) {
		customControllerComposite = new HandlerConfigurationComposite(
				parent,
				"org.jbpm.taskmgmt.def.TaskControllerHandler",
				"Choose Task Controller Handler",
				"Choose a task controller handler from the list",
				assembleInitialControllerConfigurationValues());
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		customControllerComposite.setLayoutData(layoutData);
		customControllerComposite.showDetails(true);
	}
	
	private void deleteCustomControllerComposite(Composite parent) {
		if (customControllerComposite != null) {
			customControllerComposite.dispose();
			customControllerComposite = null;
		}
	}
	
	private void createDefaultControllerComposite(Composite parent) {
		defaultControllerComposite = new DefaultControllerConfigurationComposite(parent, controllerVariables);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.horizontalSpan = 2;
		defaultControllerComposite.setLayoutData(layoutData);
	}
	
	private void deleteDefaultControllerComposite(Composite parent) {
		if (defaultControllerComposite != null) {
			defaultControllerComposite.dispose();
			defaultControllerComposite = null;
		}
	}
	
    public boolean performOk() {
		Task task = getTask();
		if (task.hasController()) {
			task.removeController();
		}
		if (!shouldAddController()) return true;
		task.addController();
		if (defaultControllerButton.getSelection()) {
			task.addControllerVariables(controllerVariables);
		} else {
			task.setControllerDelegateClassName(getControllerDelegateClassName());
			task.setControllerConfigurationType(
					HandlerConfigurationHelper.configXMLFor(getControllerConfigurationType()));
			addControllerConfigurationInfo(getControllerConfigurationData());
		}
		return true;
	}
    
	private void addControllerConfigurationInfo(Object info) {
		if (info instanceof TreeMap) {
			getTask().addControllerConfigurationInfo((TreeMap)info);
		} else {
			getTask().addControllerConfigurationInfo((String)info);			
		}
	}
	
	private boolean shouldAddController() {
		if (defaultControllerButton.getSelection()) {
			return defaultControllerComposite.getVariables().size() > 0;
		} else {
			return getControllerDelegateClassName() != null;
		}
	}
	
	private String getControllerDelegateClassName() {
		return customControllerComposite.getClassName();
	}
	
	private String getControllerConfigurationType() {
		return customControllerComposite.getConfigType();
	}
	
	private Object getControllerConfigurationData() {
		return customControllerComposite.getConfigData();
	}
	
}
