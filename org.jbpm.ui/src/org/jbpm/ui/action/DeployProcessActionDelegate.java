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
package org.jbpm.ui.action;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jbpm.ui.wizard.ProcessDeploymentWizard;

public class DeployProcessActionDelegate implements IObjectActionDelegate {

	private IWorkbenchPart targetPart;
	
	private IStructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
	
	private int openSaveProceedCancelDialog() {
        MessageDialog dialog = new MessageDialog(
        	getWorkbenchWindow().getShell(), 
        	"Save Resource", 
        	null, 
        	"'" + getResourceName() + "' has been modified. Save changes before deploying?", 
        	MessageDialog.QUESTION, 
        	new String[] { 
        		IDialogConstants.YES_LABEL, 
        		IDialogConstants.NO_LABEL,
        		IDialogConstants.CANCEL_LABEL}, 
        	0);
        return dialog.open();
		
	}
	
	private String getResourceName() {
		return ((IFolder)selection.getFirstElement()).getName();
	}
	
	private IWorkbenchWindow getWorkbenchWindow() {
		return targetPart.getSite().getWorkbenchWindow();		
	}
	
	private IEditorPart getEditorPart() {
		return getWorkbenchWindow().getActivePage().getActiveEditor();
	}
	
	private boolean cancelOrSaveAndContinue() {
		IEditorPart editor = getEditorPart();
		boolean result = true;
		if (editor.isDirty()) {
			int saveProceedCancel = openSaveProceedCancelDialog();
			if (saveProceedCancel == 2) {
				result = false;
			} else if (saveProceedCancel == 0) {
				editor.doSave(null);
			}
		}
		return result;
	}

	public void run(IAction action) {
		if (cancelOrSaveAndContinue()) {
			Shell parentShell = targetPart.getSite().getShell();
			ProcessDeploymentWizard wizard = new ProcessDeploymentWizard();
			WizardDialog wizardDialog = new WizardDialog(parentShell, wizard);
			wizard.init(PlatformUI.getWorkbench(), selection);
			wizardDialog.open();			
		}
	}


	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
	}
	
}
