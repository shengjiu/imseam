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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jbpm.ui.model.GraphElement;

public class GraphElementGeneralPropertyPage extends PropertyPage {
	
	Text nameText;
	
	public GraphElementGeneralPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}
	
	private GraphElement getGraphElement() {
		return (GraphElement)getElement();
	}

	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		createLabel(parent, "Name :");
		createNameText(parent);
		createSeparator(parent);
		setTitle("General Properties");
		return parent;
	}
	
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.heightHint = 20;
		separatorData.horizontalSpan = 2;
		separator.setLayoutData(separatorData);
	}

	private void createNameText(Composite parent) {
		nameText = new Text(parent, SWT.BORDER);
		nameText.setText(getGraphElementName());
		GridData nameTextData = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(nameTextData);
	}
	
	private String getGraphElementName() {
		String result = getGraphElement().getName();
		return result == null ? "" : result;
	}

	private void createLabel(Composite parent, String text) {
		Label nameLabel = new Label(parent, SWT.NONE);
		GridData nameLabelData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		nameLabel.setLayoutData(nameLabelData);
		nameLabel.setText(text);
	}

    public boolean performOk() {
     	handleName();
        return true;
    }
    
    private void handleName() {
    	String name = nameText.getText();
    	if (name == null || "".equals(name)) return;
    	getGraphElement().setName(name);
    }
    
}
