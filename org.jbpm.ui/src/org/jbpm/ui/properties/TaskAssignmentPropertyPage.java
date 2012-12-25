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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.model.Task;
import org.jbpm.ui.util.HandlerConfigurationHelper;

public class TaskAssignmentPropertyPage extends PropertyPage implements PluginConstants {
	
	Combo assignmentTypeCombo;
	HandlerConfigurationComposite handlerConfigurationComposite;
	SwimlaneConfigurationComposite swimlaneConfigurationComposite;
	ExpressionConfigurationComposite expressionConfigurationComposite;
	
	public TaskAssignmentPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}
	
	private Task getTask() {
		return (Task)getElement();
	}

	protected Control createContents(Composite parent) {
		createLayout(parent);
		createAssignmentTypeCombo(parent);
		createSeparator(parent);
		setTitle("Assignment");
		updateControl(parent);
		return parent;
	}
	
	private void updateControl(Composite parent) {
		Task task = getTask();
		if (task.hasSwimlane()) {
			assignmentTypeCombo.setText("Swimlane");
		} else if (task.getAssignmentExpression() != null) {
			assignmentTypeCombo.setText("Expression");
		} else if (task.hasAssignment()) {
			assignmentTypeCombo.setText("Handler");
		} 
		createAssignmentDetailsComposite(parent);
	}
	
	private void createLayout(Composite parent) {
		GridLayout layout = (GridLayout)parent.getLayout();
		layout.numColumns = 2;
	}
	
	private Map assembleInitialAssignmentConfigurationValues() {
		Map initialValues = new HashMap();
		if (getTask().hasAssignment()) {
			initialValues.put("class", getTask().getAssignmentDelegateClassName());
			String type = HandlerConfigurationHelper.configStringFor(getTask().getAssignmentConfigurationType());
			if (type != null) {
				initialValues.put("type", type);
				initialValues.put(type, getTask().getAssignmentConfigurationInfo());
			}
		}
		return initialValues;
	}
	
	private void createHandlerConfigurationComposite(Composite parent) {
		handlerConfigurationComposite = new HandlerConfigurationComposite(
				parent,
				"org.jbpm.taskmgmt.def.AssignmentHandler",
				"Choose Assignment Handler",
				"Choose an assignment handler from the list",
				assembleInitialAssignmentConfigurationValues());
		GridData handlerConfigurationCompositeData = new GridData(GridData.FILL_BOTH);
		handlerConfigurationCompositeData.horizontalSpan = 2;
		handlerConfigurationComposite.setLayoutData(handlerConfigurationCompositeData);
		handlerConfigurationComposite.showDetails("Handler".equals(assignmentTypeCombo.getText()));
	}
		
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.horizontalSpan = 2;
		separatorData.heightHint = 10;
		separator.setLayoutData(separatorData);
	}
	
	private void createAssignmentTypeCombo(final Composite parent) {
		Label assignmentTypeLabel = new Label(parent, SWT.NORMAL);
		assignmentTypeLabel.setText("Choose the assignment type: ");
		assignmentTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData assignmentTypeComboData = new GridData(GridData.FILL_HORIZONTAL);
		assignmentTypeComboData.horizontalSpan = 1;
		assignmentTypeCombo.setLayoutData(assignmentTypeComboData);
		assignmentTypeCombo.add("");
		assignmentTypeCombo.add("Swimlane");
		assignmentTypeCombo.add("Expression");
		assignmentTypeCombo.add("Handler");
		assignmentTypeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAssignmentTypeChanged(parent);
			}
		});
	}
	
	private void handleAssignmentTypeChanged(Composite parent) {
		deleteAssignmentDetailsComposite();
		createAssignmentDetailsComposite(parent);
	}
	
	private void deleteAssignmentDetailsComposite() {
		deleteHandlerConfigurationComposite();
		deleteExpressionConfigurationComposite();
		deleteSwimlaneConfigurationComposite();
	}

	private void deleteHandlerConfigurationComposite() {
		if (handlerConfigurationComposite != null) {
			handlerConfigurationComposite.dispose();
		}
		handlerConfigurationComposite = null;
	}
	
	private void deleteExpressionConfigurationComposite() {
		if (expressionConfigurationComposite != null) {
			expressionConfigurationComposite.dispose();
		}
		expressionConfigurationComposite = null;
	}
	
	private void deleteSwimlaneConfigurationComposite() {
		if (swimlaneConfigurationComposite != null) {
			swimlaneConfigurationComposite.dispose();
		}
		swimlaneConfigurationComposite = null;
	}
	
	private void createAssignmentDetailsComposite(Composite parent) {
		if ("Handler".equals(assignmentTypeCombo.getText())) {
			createHandlerConfigurationComposite(parent);
		} else if ("Expression".equals(assignmentTypeCombo.getText())) {
			createExpressionConfigurationComposite(parent);
		} else if ("Swimlane".equals(assignmentTypeCombo.getText())) {
			createSwimlaneConfigurationComposite(parent);
		}
	}
	
	private void createExpressionConfigurationComposite(Composite parent) {
		expressionConfigurationComposite = new ExpressionConfigurationComposite(parent, getTask());
		GridData expressionConfigurationCompositeData = new GridData(GridData.FILL_BOTH);
		expressionConfigurationCompositeData.horizontalSpan = 2;
		expressionConfigurationComposite.setLayoutData(expressionConfigurationCompositeData);
		parent.layout();
	}
	
	private void createSwimlaneConfigurationComposite(Composite parent) {
		swimlaneConfigurationComposite = 
			new SwimlaneConfigurationComposite(parent, getTask());
		GridData swimlaneConfigurationCompositeData = new GridData(GridData.FILL_BOTH);
		swimlaneConfigurationCompositeData.horizontalSpan = 2;
		swimlaneConfigurationComposite.setLayoutData(swimlaneConfigurationCompositeData);
		parent.layout();
	}
	
    public boolean performOk() {
		Task task = getTask();
		task.removeAssignment();
		task.setSwimlane(null);
		if ("Handler".equals(assignmentTypeCombo.getText())) {
			setAssignmentHandlerInfo(task);
		} else if ("Expression".equals(assignmentTypeCombo.getText())){
			setAssignmentExpressionInfo(task);
		} else if ("Swimlane".equals(assignmentTypeCombo.getText())) {
			setAssignmentSwimlaneInfo(task);
		} 
		return true;
	}

	private void setAssignmentHandlerInfo(Task task) {
		task.addAssignment();
		task.setAssignmentDelegateClassName(getAssignmentDelegateClassName());
		task.setAssignmentConfigurationType(
				HandlerConfigurationHelper.configXMLFor(getAssignmentConfigurationType()));
		addAssignmentConfigurationInfo(getAssignmentConfigurationData());
	}
	
	private void setAssignmentExpressionInfo(Task task) {
		task.addAssignment();
		task.setAssignmentExpression(expressionConfigurationComposite.getExpression());
	}
	
	private void setAssignmentSwimlaneInfo(Task task) {
		task.setSwimlane(swimlaneConfigurationComposite.getSwimlane());
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
	
	private void addAssignmentConfigurationInfo(Object info) {
		if (info instanceof TreeMap) {
			getTask().addAssignmentConfigurationInfo((TreeMap)info);
		} else {
			getTask().addAssignmentConfigurationInfo((String)info);			
		}
	}
	
}
