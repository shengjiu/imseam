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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.dialog.ChooseHandlerClassDialog;

public class HandlerMainInfoComposite 
extends Composite implements PluginConstants {
	
	Text classText;
	Button classButton;
	Combo configTypeCombo;
	
	String typeName;
	String title;
	String message;
	Map initialValues;

	public HandlerMainInfoComposite(
			Composite parent, String className, String title, String message, Map initialValues) {
		super(parent, SWT.NONE);
		this.typeName = className;
		this.title = title;
		this.message = message;
		this.initialValues = initialValues;
		populateControl();
	}

	private void populateControl() {
		setLayout(new GridLayout(3, false));
		createLabel("Class :");
		createClassText();
		createClassButton();
		createLabel("Configuration type :");
		createConfigTypeCombo();
	}
	
	private void createConfigTypeCombo() {
		configTypeCombo = new Combo(this, SWT.READ_ONLY);
		configTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		configTypeCombo.add(FIELD_CONFIG_TYPE);
		configTypeCombo.add(BEAN_CONFIG_TYPE);
		configTypeCombo.add(CONSTRUCTOR_CONFIG_TYPE);
		configTypeCombo.add(COMPATIBILITY_CONFIG_TYPE);	
		String initialTypeValue = (String)initialValues.get("type");
		configTypeCombo.setText(initialTypeValue == null ? FIELD_CONFIG_TYPE : initialTypeValue);
	}
	
	private void createClassButton() {
		classButton = new Button(this, SWT.NONE);
		GridData classButtonData = new GridData();
		classButtonData.minimumWidth = 50;
		classButton.setText("Browse...");
		classButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseHandlerClass();
			}
		});
	}

	private void createClassText() {
		classText = new Text(this, SWT.BORDER);
		GridData classTextData = new GridData(GridData.FILL_HORIZONTAL);
		classText.setLayoutData(classTextData);
		String initialName = (String)initialValues.get("class");
		classText.setText(initialName == null ? "" : initialName);
	}

	private void createLabel(String text) {
		Label nameLabel = new Label(this, SWT.NONE);
		GridData nameLabelData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		nameLabel.setLayoutData(nameLabelData);
		nameLabel.setText(text);
	}
	
	private void chooseHandlerClass() {
		ChooseHandlerClassDialog dialog = 
			new ChooseHandlerClassDialog(
					getShell(), 
					typeName,
					title,
					message);
		String className = dialog.openDialog();
		if (className != null) {
			classText.setText(className);
		}
	}
	
	public void setEnabled(boolean enabled) {
		classText.setEnabled(enabled);
		classButton.setEnabled(enabled);
		configTypeCombo.setEnabled(enabled);
	}
	
	public void setConfigTypeListener(Listener listener) {
		configTypeCombo.addListener(SWT.Selection, listener);
	}
	
	public void setClassTextListener(Listener listener) {
		classText.addListener(SWT.Modify, listener);
	}
	
	public String getHandlerClassName() {
		return classText.getText();
	}
	
	public String getConfigType() {
		return configTypeCombo.getText();
	}
	
}
