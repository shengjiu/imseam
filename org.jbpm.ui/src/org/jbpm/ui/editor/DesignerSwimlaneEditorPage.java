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
package org.jbpm.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.jbpm.ui.editor.form.swimlane.SwimlaneDetailsForm;
import org.jbpm.ui.editor.form.swimlane.SwimlaneMasterForm;
import org.jbpm.ui.model.Swimlane;

public class DesignerSwimlaneEditorPage extends EditorPart {
	
	SwimlaneDetailsForm swimlaneDetailsForm;
	SwimlaneMasterForm swimlaneMasterForm;
	DesignerEditor editor;
	
	public DesignerSwimlaneEditorPage(DesignerEditor editor) {
		this.editor = editor;
	}
		
	public void createPartControl(Composite parent) {		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Swimlanes");
		setPartLayout(form);
		createMaster(toolkit, form.getBody());				
		createDetails(toolkit, form.getBody());						
	}

	private void setPartLayout(ScrolledForm form) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
	}

	private void createDetails(FormToolkit toolkit, Composite composite) {
		swimlaneDetailsForm = new SwimlaneDetailsForm(toolkit, composite);
		swimlaneDetailsForm.create();
		swimlaneDetailsForm.setSwimlaneMasterForm(swimlaneMasterForm);
	}
	
	private void createMaster(FormToolkit toolkit, Composite form) {
		swimlaneMasterForm = new SwimlaneMasterForm(toolkit, form);
		swimlaneMasterForm.setProcessDefinition(editor.getProcessDefinition());
		swimlaneMasterForm.create();
		swimlaneMasterForm.addSelectionChangedListener(new SwimlaneSelectionChangedListener());
	}
	
	public void setFocus() {	
	}
	
	public void doSave(IProgressMonitor monitor) {
	}
	
	public void doSaveAs() {
	}

	public boolean isDirty() {
		return false;
	}
	
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}
		
	class SwimlaneSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			swimlaneDetailsForm.update(
					(Swimlane)((StructuredSelection)event.getSelection()).getFirstElement());
		}		
	}
	
}
