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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.util.ProjectFinder;

public class HandlerDetailsComposite 
extends Composite implements PluginConstants{
	
	private static final String FIELD_CONFIG_LABEL = 
		"Select the appropriate fields and specify the configuration value :";
	private static final String BEAN_CONFIG_LABEL = 
		"Select the appropriate setters and specify the configuration value :";
	private static final String STRING_CONFIG_LABEL = 
		"Enter the configuration string :";
	private static final String NO_FIELDS = 
		"The selected class has no fields to configure.";
	private static final String NO_SETTERS = 
		"The selected class has no setters.";
	private static final String UNKNOWN_CLASS =
		"The selected class does not exist in your project classpath.";
	
	Label configurationDetailsLabel;
	Text configurationDetailsText;
	Composite configurationDetailsTable;
	String configType;
	IType actionClass;
	
	TreeMap fieldConfigurationElements;
	TreeMap beanConfigurationElements;
	String constructorConfigurationString;
	String compatibilityConfigurationString;
	
	List configurableElements;
	
	public HandlerDetailsComposite(
			Composite parent, Map initialValues) {
		super(parent, SWT.NONE);
		fieldConfigurationElements = getTreeMapNotNull(FIELD_CONFIG_TYPE, initialValues);
		beanConfigurationElements = getTreeMapNotNull(BEAN_CONFIG_TYPE, initialValues);
		constructorConfigurationString = (String)initialValues.get(CONSTRUCTOR_CONFIG_TYPE);
		compatibilityConfigurationString = (String)initialValues.get(COMPATIBILITY_CONFIG_TYPE);		
		configType = (String)initialValues.get("type");
		configType = configType == null ? FIELD_CONFIG_TYPE : configType;
		setHandlerClass((String)initialValues.get("class"));		
	}
	
	private TreeMap getTreeMapNotNull(String key, Map map) {
		TreeMap result = (TreeMap)map.get(key);
		if (result == null) {
			result = new TreeMap();
		}
		return result;
	}
	
	private void populateControl() {
		setLayout(new GridLayout(1, false));
		createConfigurationDetailsLabel();
		createConfigurationDetailsArea();
	}
	
	private void createConfigurationDetailsLabel() {
		configurationDetailsLabel = new Label(this, SWT.NONE);
		configurationDetailsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		configurationDetailsLabel.setText(getLabelText());	
	}
	
	private void createConfigurationDetailsArea() {
		if (isTableConfigType()) {
			createConfigurationTable();
		} else {
			createConfigurationText();
		}
	}
	
	private void createConfigurationTable() {
		configurableElements = getConfigurableElements();
		if (configurableElements.size() == 0) return;
		configurationDetailsTable = new HandlerDetailsTable(
				this, 
				configType, 
				getConfiguredElements(), 
				getConfigurableElements());
		GridData configurationDetailsTableData = new GridData(GridData.FILL_HORIZONTAL);
		configurationDetailsTableData.heightHint = 100;
    	configurationDetailsTable.setLayoutData(configurationDetailsTableData);
	}
	
	private void createConfigurationText() {
		configurationDetailsText = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		GridData configurationDetailsTextData = new GridData(GridData.FILL_HORIZONTAL);
		configurationDetailsTextData.heightHint = 75;
		configurationDetailsText.setLayoutData(configurationDetailsTextData);
		configurationDetailsText.setText(getConfigurationDetailsString());
		configurationDetailsText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleConfigurationTextChanged();				
			}			
		});
	}
	
	private String getConfigurationDetailsString() {
		String result = null;
		if (CONSTRUCTOR_CONFIG_TYPE.equals(configType)) {
			result = constructorConfigurationString;
		} else {
			result = compatibilityConfigurationString;
		}
		return result == null ? "" : result;
	}
	
	private void handleConfigurationTextChanged() {
		if (CONSTRUCTOR_CONFIG_TYPE.equals(configType)) {
			constructorConfigurationString = configurationDetailsText.getText();
		} else {
			compatibilityConfigurationString = configurationDetailsText.getText();
		}
	}
	
	private boolean isTableConfigType() {
		return BEAN_CONFIG_TYPE.equals(configType) || FIELD_CONFIG_TYPE.equals(configType);
	}

	private String getLabelText() {
		if (actionClass == null) {
			return UNKNOWN_CLASS;
		} else if (BEAN_CONFIG_TYPE.equals(configType)) { 
			return getConfigurableElements().size() > 0 ? BEAN_CONFIG_LABEL : NO_SETTERS;
		} else if (FIELD_CONFIG_TYPE.equals(configType)) {
			return getConfigurableElements().size() > 0 ? FIELD_CONFIG_LABEL : NO_FIELDS;
		} else {
			return STRING_CONFIG_LABEL;
		}
	}
	
	public void setConfigType(String configType) {
		this.configType = configType;
		updateControl();
	}
	
	public void setHandlerClass(String className) {
		actionClass = getClassFor(className);
		updateControl();
	}
	
	private IType getClassFor(String className) {
		if (className == null) return null;
		try {
			return ProjectFinder.getCurrentProject().findType(className);
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private TreeMap getConfiguredElements() {
		if (BEAN_CONFIG_TYPE.equals(configType)) {
			return beanConfigurationElements;
		} else {
			return fieldConfigurationElements;
		}
	}
	
	private List getConfigurableElements() {
		List result = new ArrayList();
		if (actionClass == null) return result;
		if (FIELD_CONFIG_TYPE.equals(configType)) {
			addFields(result);
		} else {
			addSetters(result);
		}
		Collections.sort(result);
		return result;
	}

	private void addSetters(List result) {
		try {
			IMethod[] methods = actionClass.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getElementName().startsWith("set")) {
					StringBuffer buff = new StringBuffer(methods[i].getElementName().substring(3));
					buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));
					result.add(buff.toString());
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void addFields(List result) {
		try {
			IField[] fields = actionClass.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (!Flags.isStatic(fields[i].getFlags())) {
					result.add(fields[i].getElementName());
				}
			}
		} catch (JavaModelException  e) {
			e.printStackTrace();
		}
	}
		
	private void updateControl() {
		disposeChildren();
		populateControl(); 
		layout();
		getParent().layout();
	}

	private void disposeChildren() {
		if (configurationDetailsLabel != null) configurationDetailsLabel.dispose();
		if (configurationDetailsTable != null) configurationDetailsTable.dispose();
		if (configurationDetailsText != null) configurationDetailsText.dispose();
	}
	
	public TreeMap getFieldConfigurationElements() {
		return fieldConfigurationElements;
	}
	
	public TreeMap getBeanConfigurationElements() {
		return beanConfigurationElements;
	}
	
	public String getConstructorConfigurationString() {
		return constructorConfigurationString;
	}
	
	public String getCompatibilityConfigurationString() {
		return compatibilityConfigurationString;
	}
	
}
