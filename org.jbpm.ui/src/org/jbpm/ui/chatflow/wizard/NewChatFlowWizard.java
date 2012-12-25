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
package org.jbpm.ui.chatflow.wizard;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewChatFlowWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private NewChatFlowWizardPage page;
	
	public NewChatFlowWizard() {
		setWindowTitle("New Chat Flow");
	}

	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		this.selection= currentSelection;
	}
	
	public void addPages() {
		page = new NewChatFlowWizardPage();
		addPage(page);
		page.init(selection);
	}
	
	public boolean performFinish() {
		try {
			IFolder folder = page.getProcessFolder();
			IFile chatFlowFile = page.getChatFlowFile();
			chatFlowFile.create(createInitialChatFlow(), true, null);
			IFile gpdFile = folder.getFile(".gpd." + chatFlowFile.getName());
			gpdFile.create(createInitialGpdInfo(), true, null);
			IDE.openEditor(getActivePage(), chatFlowFile);
			BasicNewResourceWizard.selectAndReveal(gpdFile, getActiveWorkbenchWindow());
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}

	private IWorkbenchPage getActivePage() {
		return getActiveWorkbenchWindow().getActivePage();
	}

	private IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	private ByteArrayInputStream createInitialChatFlow() throws JavaModelException {
		String name = page.getChatFlowFile().getName();
		int index = name.lastIndexOf(".xml");
		name = name.substring(0, index);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append(
				"<chatflow-definition\n" +
				"  name=\"" + name + "\">\n" +	
				"</chatflow-definition>");	
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

	private ByteArrayInputStream createInitialGpdInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<chatflow-diagram />");	
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}
	
}
