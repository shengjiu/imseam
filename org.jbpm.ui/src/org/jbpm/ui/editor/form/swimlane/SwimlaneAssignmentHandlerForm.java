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
package org.jbpm.ui.editor.form.swimlane;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.jbpm.ui.DesignerLogger;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.dialog.ChooseHandlerClassDialog;
import org.jbpm.ui.model.Swimlane;
import org.jbpm.ui.util.HandlerConfigurationHelper;
import org.jbpm.ui.util.ProjectFinder;

public class SwimlaneAssignmentHandlerForm implements PluginConstants {
	
	private static final String FIELD_CONFIG_LABEL = 
		"Select the appropriate fields and specify the configuration value:";
	private static final String BEAN_CONFIG_LABEL = 
		"Select the appropriate setters and specify the configuration value:";
	private static final String STRING_CONFIG_LABEL = 
		"Enter the configuration string:";
	private static final String NO_FIELDS = 
		"The selected class has no fields to configure.";
	private static final String NO_SETTERS = 
		"The selected class has no setters.";
	private static final String UNKNOWN_CLASS =
		"The selected class does not exist in your project classpath.";
	
	private FormToolkit toolkit;
	private Section section;
	private Swimlane swimlane;
	
	private Composite formClient;
	private Label handlerMessageLabel;
	private SwimlaneAssignmentHandlerConfigurationTable configurationTable;
	private Text configurationText;
	private Label dummy1, dummy2;
	private CCombo configTypeCombo;
	private Text classNameText;
	
	public SwimlaneAssignmentHandlerForm(FormToolkit toolkit, Section section, Swimlane swimlane) {
		this.toolkit = toolkit;
		this.section = section;
		this.swimlane = swimlane;
	}
	
	public void create() {
		createFormClient();		
		createHandlerClassField();		
		createConfigTypeField();		
		createHandlerMessageField();		
		updateConfigurationDetailsField();
		layout();		
	}

	private void layout() {
		formClient.layout();
		section.getParent().layout();
	}
	
	private String getHandlerMessage() {
		IType type = getClassFor(swimlane.getAssignmentDelegateClassName());
		if (type == null) return UNKNOWN_CLASS;
		String configType = configTypeCombo.getText();
		if (configType.equals(CONSTRUCTOR_CONFIG_TYPE) || configType.equals(COMPATIBILITY_CONFIG_TYPE)) return STRING_CONFIG_LABEL;
		if (configType.equals(FIELD_CONFIG_TYPE)) {
			return getFields(type).isEmpty() ? NO_FIELDS : FIELD_CONFIG_LABEL;				
		}
		return getSetters(type).isEmpty() ? NO_SETTERS : BEAN_CONFIG_LABEL;
	}
	
	private List getFields(IType type) {
		List result = new ArrayList();
		try {
			IField[] fields = type.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (!Flags.isStatic(fields[i].getFlags())) {
					result.add(fields[i].getElementName());
				}
			}
		} catch (JavaModelException  e) {
			DesignerLogger.logError("Error while getting the fields for type " + type + ".", e);
		}
		return result;
	}
	
	private List getSetters(IType type) {
		List result = new ArrayList();
		try {
			IMethod[] methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getElementName().startsWith("set")) {
					StringBuffer buff = new StringBuffer(methods[i].getElementName().substring(3));
					buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));
					result.add(buff.toString());
				}
			}
		} catch (JavaModelException  e) {
			DesignerLogger.logError("Error while getting the setters for type " + type + ".", e);
		}
		return result;
	}
	
	private void updateHandlerMessageField() {
		handlerMessageLabel.setText(getHandlerMessage());		
	}
	
	private void updateConfigurationDetailsField() {
		removeConfigurationDetailsField();
		createConfigurationDetailsField();
		createTrailingDummy();
	}
	
	private void createConfigurationDetailsField() {
		IType type = getClassFor(swimlane.getAssignmentDelegateClassName());
		if (type == null) return;
		String configType = configTypeCombo.getText();
		if (configType.equals(FIELD_CONFIG_TYPE) && !getFields(type).isEmpty()) {
			createConfigurationTableField();
		} else if (configType.equals(BEAN_CONFIG_TYPE) && !getSetters(type).isEmpty()) {
			createConfigurationTableField();
		} else if (configType.equals(CONSTRUCTOR_CONFIG_TYPE) || configType.equals(COMPATIBILITY_CONFIG_TYPE)){
			createConfigurationTextField();
		}
	}
	
	private void createConfigurationTextField() {
		dummy1 = toolkit.createLabel(formClient, "");
		configurationText = toolkit.createText(formClient, "", SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 50;
		configurationText.setLayoutData(gridData);		
		configurationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				swimlane.setAssignmentConfigurationString(configurationText.getText());
			}			
		});
		String configString = swimlane.getAssignmentConfigurationString();
		configurationText.setText(configString == null ? "" : configString);
	}

	private void createConfigurationTableField() {
		dummy1 = toolkit.createLabel(formClient, "");
		configurationTable = new SwimlaneAssignmentHandlerConfigurationTable(
				toolkit, formClient, swimlane, getTableRows());
		configurationTable.create();
	}
	
	private List getTableRows() {
		IType type = getClassFor(swimlane.getAssignmentDelegateClassName());
		if (type == null) return new ArrayList();
		String configType = configTypeCombo.getText();
		if (configType.equals(FIELD_CONFIG_TYPE)) {
			return getFields(type);
		} else if (configType.equals(BEAN_CONFIG_TYPE)) {
			return getSetters(type);
		} else {
			return new ArrayList();
		}		
	}

	private void createHandlerMessageField() {
		toolkit.createLabel(formClient, "");		
		handlerMessageLabel = toolkit.createLabel(formClient, "", SWT.WRAP);
		GridData gridData6 = new GridData(GridData.FILL_HORIZONTAL);
		gridData6.horizontalSpan = 2;
		handlerMessageLabel.setLayoutData(gridData6);
		handlerMessageLabel.setText(getHandlerMessage());
		handlerMessageLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
	}
	
	private void createConfigTypeField() {
		Label configTypeLabel = toolkit.createLabel(formClient, "Type:");
		configTypeLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		configTypeCombo = new CCombo(formClient, SWT.DROP_DOWN | SWT.FLAT);
		configTypeCombo.add(FIELD_CONFIG_TYPE);
		configTypeCombo.add(BEAN_CONFIG_TYPE);
		configTypeCombo.add(CONSTRUCTOR_CONFIG_TYPE);
		configTypeCombo.add(COMPATIBILITY_CONFIG_TYPE);
		configTypeCombo.setEditable(false);
		toolkit.adapt(configTypeCombo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		configTypeCombo.setLayoutData(gridData);		
		if (swimlane != null) {
			String configType = swimlane.getAssignmentConfigurationType();
			if (configType == null) {
				swimlane.setAssignmentConfigurationType(FIELD_CONFIG_TYPE);
			} 
			configTypeCombo.setText(HandlerConfigurationHelper.configStringFor(swimlane.getAssignmentConfigurationType()));
			configTypeCombo.setSelection(new Point(0,0));
		}
		configTypeCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				swimlane.setAssignmentConfigurationType(
						HandlerConfigurationHelper.configXMLFor(configTypeCombo.getText()));
				updateHandlerMessageField();
				updateConfigurationDetailsField();
				layout();
			}
		});
	}

	private void createHandlerClassField() {
		Label classNameLabel = toolkit.createLabel(formClient, "Class:");
		classNameLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		classNameText = toolkit.createText(formClient, "");
		String className = swimlane.getAssignmentDelegateClassName();
		classNameText.setText(className == null ? "" : className);
		GridData gridData3 = new GridData(GridData.FILL_HORIZONTAL);
		classNameText.setLayoutData(gridData3);
		classNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String className = ((Text)e.widget).getText();
				if (swimlane != null) {
					swimlane.setAssignmentDelegateClassName(className);
				}
				updateHandlerMessageField();
				updateConfigurationDetailsField();
			}			
		});		
		Button searchButton = toolkit.createButton(formClient, "Browse...", SWT.PUSH);
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseHandlerClass();				
			}
		});
	}

	private void chooseHandlerClass() {
		ChooseHandlerClassDialog dialog = 
			new ChooseHandlerClassDialog(
					null, 
					"org.jbpm.taskmgmt.def.AssignmentHandler",
					"Choose Assignment Handler",
					"Choose an assignment handler from the list");
		String className = dialog.openDialog();
		if (className != null) {
			classNameText.setText(className);
		}
	}
	
	private void createFormClient() {
		formClient =  toolkit.createComposite(section);
		toolkit.paintBordersFor(formClient);
		section.setClient(formClient);
		section.setDescription("Configure the assignment handler for this swimlane.");		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.numColumns = 3;
		formClient.setLayout(layout);
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
			
	private void removeConfigurationDetailsField() {
		if (configurationTable != null) {
			configurationTable.dispose();
			configurationTable = null;
		}
		if (configurationText != null) {
			configurationText.dispose();
			configurationText = null;
		}
		if (dummy1 != null) {
			dummy1.dispose();
			dummy1 = null;
		}
		if (dummy2 != null) {
			dummy2.dispose();
			dummy2 = null;
		}
	}

	private void createTrailingDummy() {
		dummy2 = toolkit.createLabel(formClient, "");
		layout();
		section.layout();
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		dummy2.setLayoutData(gridData);
	}
	
	public void dispose() {
		formClient.dispose();
		formClient = null;
		handlerMessageLabel = null;		
		configurationTable = null;
		dummy1 = null;
		dummy2 = null;
	}
	
}
