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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.jbpm.ui.model.Swimlane;

public class SwimlaneAssignmentExpressionForm {

	private FormToolkit toolkit;
	private Section section;
	private Swimlane swimlane;
	
	private Composite formClient;
	private Text assignmentExpressionText;

	public SwimlaneAssignmentExpressionForm(FormToolkit toolkit, Section section, Swimlane swimlane) {
		this.toolkit = toolkit;
		this.section = section;
		this.swimlane = swimlane;
	}
	
	public void create() {
		createFormClient();		
		createAssignmentExpressionTextField();		
		layout();
	}

	private void layout() {
		formClient.layout();
		section.getParent().layout();
	}

	private void createAssignmentExpressionTextField() {
		Label expressionLabel = toolkit.createLabel(formClient, "Expression:");
		expressionLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		assignmentExpressionText = toolkit.createText(formClient, "");
		String expression = swimlane.getAssignmentExpression();
		assignmentExpressionText.setText(expression == null ? "" : expression);
		assignmentExpressionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String expression = ((Text)e.getSource()).getText();
				if (swimlane != null) {
					swimlane.setAssignmentExpression(expression);
				}
			}			
		});
		GridData gridData = new GridData(GridData.FILL_BOTH);
		assignmentExpressionText.setLayoutData(gridData);
		toolkit.createLabel(formClient, "");		
	}

	private void createFormClient() {
		formClient = toolkit.createComposite(section);
		toolkit.paintBordersFor(formClient);
		section.setClient(formClient);
		section.setDescription("Specify the assignment expression for this swimlane.");

		GridLayout layout = new GridLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.numColumns = 2;
		formClient.setLayout(layout);
	}
	
	public void dispose() {
		formClient.dispose();
	}
	
	
}
