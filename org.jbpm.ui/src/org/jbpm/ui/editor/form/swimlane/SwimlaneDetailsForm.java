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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.jbpm.ui.model.Swimlane;

public class SwimlaneDetailsForm {

	public static final int NONE = 0;
	public static final int EXPRESSION = 1;
	public static final int HANDLER = 2;
	
	private FormToolkit toolkit;
	private Composite composite;
	private Swimlane swimlane;
	
	private Form detailsForm;
	private SwimlaneAssignmentForm swimlaneAssignmentForm;
	private SwimlaneMasterForm swimlaneMasterForm;
	private Text nameText;
	private CCombo assignmentTypeCombo;
	
	public SwimlaneDetailsForm(FormToolkit toolkit, Composite composite) {
		this.toolkit = toolkit;
		this.composite = composite;
	}	
	
	public void setSwimlaneMasterForm(SwimlaneMasterForm masterForm) {
		this.swimlaneMasterForm = masterForm;
	}
	
	public void create() {
		createForm();		
		createGeneralitiesForm();
		createAssignmentForm();	
	}
	
	private void createForm() {
		detailsForm = toolkit.createForm(composite);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		detailsForm.setLayoutData(layoutData);		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		detailsForm.getBody().setLayout(layout);
	}
	
	private void createAssignmentForm() {
		swimlaneAssignmentForm = new SwimlaneAssignmentForm(toolkit, detailsForm.getBody());
		swimlaneAssignmentForm.create();
	}
	
	private void createGeneralitiesForm() {		
		Composite client = createFormClient();		
		createNameField(client);
		createTypeField(client);		
	}

	private Composite createFormClient() {
		Section swimlaneDetails = toolkit.createSection(detailsForm.getBody(), Section.TITLE_BAR | Section.DESCRIPTION);
		swimlaneDetails.marginWidth = 5;
		swimlaneDetails.setText("Swimlane Details");
		swimlaneDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite detailClient =  toolkit.createComposite(swimlaneDetails);
		swimlaneDetails.setClient(detailClient);
		swimlaneDetails.setDescription("Set the name and assignment type of the selected swimlane.");
		toolkit.paintBordersFor(detailClient);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.numColumns = 3;
		detailClient.setLayout(layout);
		return detailClient;
	}

	private void createTypeField(Composite detailClient) {
		Label label = toolkit.createLabel(detailClient, "Assignment Type:");
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		assignmentTypeCombo = new CCombo(detailClient, SWT.DROP_DOWN | SWT.FLAT);
		assignmentTypeCombo.add("");
		assignmentTypeCombo.add("Expression");
		assignmentTypeCombo.add("Handler");
		assignmentTypeCombo.setEditable(false);
		assignmentTypeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (swimlane == null) return;
				if (assignmentTypeCombo.getSelectionIndex() == NONE) {
					swimlane.removeAssignment();
				} else if (assignmentTypeCombo.getSelectionIndex() == EXPRESSION) {
					swimlane.addAssignment();
					swimlane.setAssignmentDelegateClassName(null);
					swimlane.setAssignmentExpression("");
				} else {
					swimlane.addAssignment();
					swimlane.setAssignmentExpression(null);
					swimlane.setAssignmentDelegateClassName("");
				}
				swimlaneAssignmentForm.update(swimlane);
			}			
		});
		toolkit.adapt(assignmentTypeCombo);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		assignmentTypeCombo.setLayoutData(gridData);
	}

	private void createNameField(Composite detailClient) {
		Label nameLabel = toolkit.createLabel(detailClient, "Name:");
		nameLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		nameText = toolkit.createText(detailClient, "");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		nameText.setLayoutData(gridData);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.getSource()).getText();
				if (swimlane != null && !swimlane.getName().equals(name)) {
					swimlane.setName(name);
					swimlaneMasterForm.swimlaneTable.update(swimlane, null);
				}
			}			
		});
	}
	
	public void update(Swimlane swimlane) {
		this.swimlane = swimlane;
		nameText.setText(swimlane == null ? "" : swimlane.getName());
		if (swimlane == null || swimlane.getAssignment() == null) {
			assignmentTypeCombo.select(NONE);
		} else if (swimlane.getAssignmentExpression() == null) {
			assignmentTypeCombo.select(HANDLER);
		} else {
			assignmentTypeCombo.select(EXPRESSION);
		}
		swimlaneAssignmentForm.update(swimlane);
	}
	
}
