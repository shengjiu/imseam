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
package org.jbpm.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.jbpm.ui.DesignerLogger;
import org.jbpm.ui.prefs.JbpmInstallation;
import org.jbpm.ui.prefs.PreferencesManager;

public class NewProcessDefinitionWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private NewProcessDefinitionWizardPage page;
	
	public NewProcessDefinitionWizard() {
		setWindowTitle("New Process Definition");
	}

	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		this.selection= currentSelection;
	}
	
	public void addPages() {
		page = new NewProcessDefinitionWizardPage();
		addPage(page);
		page.init(selection);
	}
	
	public boolean performFinish() {
		try {
			IFolder folder = page.getProcessFolder();
			folder.create(true, true, null);
			IFile processDefinitionFile = folder.getFile("processdefinition.xml");
			processDefinitionFile.create(createInitialProcessDefinition(), true, null);
			IFile gpdFile = folder.getFile("gpd.xml");
			gpdFile.create(createInitialGpdInfo(), true, null);
			IDE.openEditor(getActivePage(), gpdFile);
			openPropertiesView();
			BasicNewResourceWizard.selectAndReveal(gpdFile, getActiveWorkbenchWindow());
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void openPropertiesView() {
		try {
			if (getActivePage().findView("org.eclipse.ui.views.PropertySheet") == null) {
				getActivePage().showView("org.eclipse.ui.views.PropertySheet");
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private IWorkbenchPage getActivePage() {
		return getActiveWorkbenchWindow().getActivePage();
	}

	private IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	private ByteArrayInputStream createInitialProcessDefinition() throws JavaModelException {
		String parName = page.getProcessFolder().getName();
		String processName = parName; //.substring(0, parName.indexOf(".par"));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append(
				"<process-definition\n" +
				"  xmlns=\"" + getJbpmSchemaNameSpace() + "\"" +
				"  name=\"" + processName + "\">\n" +	
				"</process-definition>");	
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

	private ByteArrayInputStream createInitialGpdInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<process-diagram></process-diagram>");	
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}
	
	private String getJbpmSchemaNameSpace() throws JavaModelException {
		try {
			IProject project = page.getProcessFolder().getProject();
			String jbpmName = project.getPersistentProperty(new QualifiedName("", "jbpmName"));
			if (jbpmName == null) return "";
			JbpmInstallation jbpmInstallation = PreferencesManager.INSTANCE.getJbpmInstallation(jbpmName);
			if (jbpmInstallation == null) return "";
			File file = new Path(jbpmInstallation.location).append("src/resources/gpd/version.info.xml").toFile();
			if (!file.exists()) return "";
			return getNameSpace(file);
		} catch (CoreException e) {
			return "";
		}
	}
	
	private String getNameSpace(File file) {
		try {
			Document document = new SAXReader().read(file);
			Attribute attribute = document.getRootElement().attribute("namespace");
			if (attribute != null) {
				return attribute.getValue();
			} else {
				return "";
			}
		} catch (DocumentException e) {
			DesignerLogger.logError(e);
			return "";
		} catch (MalformedURLException e) {
			DesignerLogger.logError(e);
			return "";
		}
	}

}
