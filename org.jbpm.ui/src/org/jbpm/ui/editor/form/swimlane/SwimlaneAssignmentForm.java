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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.jbpm.ui.model.Swimlane;

public class SwimlaneAssignmentForm {

	private FormToolkit toolkit;
	private Composite composite;
	private Composite client;
	
	private Section section;
	private SwimlaneAssignmentHandlerForm assignmentHandlerDetailsForm;
	private SwimlaneAssignmentExpressionForm assignmentExpressionDetailsForm;
	
	public SwimlaneAssignmentForm(FormToolkit toolkit, Composite composite) {
		this.toolkit = toolkit;
		this.composite = composite;
	}	
	
	public void create() {
		section = toolkit.createSection(composite, Section.TITLE_BAR | Section.DESCRIPTION);
		section.marginWidth = 5;
		section.setText("Assignment Details");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		createDummyClient();
	}

	private void createDummyClient() {
		client = toolkit.createComposite(section);
		section.setClient(client);
		section.setDescription("");
	}
	
	public void update(Swimlane swimlane) {
		deleteDetailsClients();
		if (swimlane != null && swimlane.getAssignment() != null) {
			if (swimlane.getAssignmentExpression() != null) {
				createAssignmentExpressionDetailsClient(swimlane);
			} else {
				createAssignmentHandlerDetailsClient(swimlane);
			}
		} else {
			createDummyClient();
		}
		composite.layout();
		section.layout();
	}
	
	private void createAssignmentExpressionDetailsClient(Swimlane swimlane) {
		assignmentExpressionDetailsForm = new SwimlaneAssignmentExpressionForm(toolkit, section, swimlane);
		assignmentExpressionDetailsForm.create();
	}

	private void createAssignmentHandlerDetailsClient(Swimlane swimlane) {
		assignmentHandlerDetailsForm = new SwimlaneAssignmentHandlerForm(toolkit, section, swimlane);
		assignmentHandlerDetailsForm.create();
	}
	
	private void deleteDetailsClients() {
		if (client != null) {
			client.dispose();
			client = null;
		}
		if (assignmentHandlerDetailsForm != null) {
			assignmentHandlerDetailsForm.dispose();
			assignmentHandlerDetailsForm = null;
		}
		if (assignmentExpressionDetailsForm != null) {
			assignmentExpressionDetailsForm.dispose();
			assignmentExpressionDetailsForm = null;
		}
	}
	
}
