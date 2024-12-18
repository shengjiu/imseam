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

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.jbpm.ui.editor.form.deployment.DeploymentForm;

public class DesignerDeploymentEditorPage extends EditorPart {
	
	DesignerEditor editor;
	DeploymentForm deploymentForm;
	
	public DesignerDeploymentEditorPage(DesignerEditor editor) {
		this.editor = editor;
	}
		
	public void createPartControl(Composite parent) {		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Deployment");
		setPartLayout(form);
		createForm(toolkit, form.getBody());
	}

	private void setPartLayout(ScrolledForm form) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
	}

	private void createForm(FormToolkit toolkit, Composite form) {
		deploymentForm = new DeploymentForm(toolkit, form, getProcessFolder());
		deploymentForm.create();
		editor.getProcessDefinition().addObserverOfClassNameChanges(deploymentForm);
	}
	
	private IFolder getProcessFolder() {
		IFile file = ((IFileEditorInput)editor.getEditorInput()).getFile();
		if (file.getParent() instanceof IFolder) {
			return (IFolder)file.getParent();
		}
		return null;
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
	
	public void refreshForm(ArrayList objectsToRefresh) {
		deploymentForm.refresh(objectsToRefresh);
	}
	
	public void dispose() {
		editor.getProcessDefinition().removeObserverOfClassNameChanges(deploymentForm);
		super.dispose();
	}
	
}
