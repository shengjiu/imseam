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

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.model.ProcessDefinition;
import org.jbpm.ui.model.Swimlane;
import org.jbpm.ui.model.Task;

public class SwimlaneConfigurationComposite extends Composite implements PluginConstants {
	
	Combo swimlaneCombo;
	
	public SwimlaneConfigurationComposite(Composite parent, Task task) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));
		createComposite(task);
		createSeparator();
		updateControl();
	}
	
	private void updateControl() {
		getParent().layout();
	}
	
	private void createComposite(Task task) {
		ProcessDefinition process = task.getProcessDefinition();
		Swimlane swimlane = task.getSwimlane();
		Label expressionLabel = new Label(this, SWT.NORMAL);
		expressionLabel.setText("Choose a swimlane : ");
		swimlaneCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY );
		swimlaneCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		swimlaneCombo.add("");
		Iterator iterator = process.getSwimlanes().iterator();
		while (iterator.hasNext()) {
			swimlaneCombo.add(((Swimlane)iterator.next()).getName());
		}
		swimlaneCombo.setText(swimlane == null ? "" : swimlane.getName());
	}
	
	private void createSeparator() {
		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.horizontalSpan = 2;
		separatorData.heightHint = 10;
		separator.setLayoutData(separatorData);
	}
	
	public String getSwimlane() {
		String result = swimlaneCombo.getText();
		return "".equals(result) ? null : result;
	}
		
}
