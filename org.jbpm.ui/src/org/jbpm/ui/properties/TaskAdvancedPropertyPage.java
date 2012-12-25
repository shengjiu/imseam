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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jbpm.ui.model.Task;

public class TaskAdvancedPropertyPage extends PropertyPage {
	
	Text duedateText;
	Button blockingButton;
	
	public TaskAdvancedPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}
	
	private Task getTask() {
		return (Task)getElement();
	}

	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		createLabel(parent, "Due date :");
		createDuedateText(parent);
		createLabel(parent, "Blocking :");
		createBlockingButton(parent);
		createSeparator(parent);
		setTitle("Advanced Properties");
		return parent;
	}
	
	private void createDuedateText(Composite parent) {
		duedateText = new Text(parent, SWT.BORDER);
		duedateText.setText(getTaskDuedate());
		GridData duedateTextData = new GridData(GridData.FILL_HORIZONTAL);
		duedateText.setLayoutData(duedateTextData);
	}
	
	private String getTaskDuedate() {
		String result = getTask().getDueDate();
		return result == null ? "" : result;
	}

	private void createBlockingButton(Composite parent) {
		blockingButton = new Button(parent, SWT.CHECK);
		blockingButton.setSelection(getTask().isBlocking());
	}

	private void createLabel(Composite parent, String text) {
		Label nameLabel = new Label(parent, SWT.NONE);
		GridData nameLabelData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		nameLabel.setLayoutData(nameLabelData);
		nameLabel.setText(text);
	}

	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.heightHint = 20;
		separatorData.horizontalSpan = 2;
		separator.setLayoutData(separatorData);
	}

    public boolean performOk() {
     	handleDuedate();
     	handleBlocking();
        return true;
    }
    
    private void handleDuedate() {
    	String duedate = duedateText.getText();
    	if ("".equals(duedate)) {
    		duedate = null;
    	}
    	getTask().setDueDate(duedate);
    }
    
    private void handleBlocking() {
    	getTask().setBlocking(blockingButton.getSelection());
    }

}
