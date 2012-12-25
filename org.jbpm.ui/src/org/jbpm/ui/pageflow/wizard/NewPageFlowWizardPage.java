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
package org.jbpm.ui.pageflow.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class NewPageFlowWizardPage extends WizardPage {
	
	private Text containerText;
	private Text fileNameText;
	private Button browseButton;
	
	private IWorkspaceRoot workspaceRoot;
	private String containerName;

	public NewPageFlowWizardPage() {
		super("Page Flow");
		setTitle("Create Page Flow");
		setDescription("Create a new page flow");	
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	}

	public void init(IStructuredSelection selection) {
		IJavaElement javaElement= getInitialJavaElement(selection);		
		initContainerName(javaElement);
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = createClientArea(parent);		
		createLabel(composite);	
		createContainerField(composite);
		createFileNameField(composite);
		setControl(composite);
		Dialog.applyDialogFont(composite);		
		setPageComplete(false);
	}

	private void createLabel(Composite composite) {
		Label label= new Label(composite, SWT.WRAP);
		label.setText("Choose a source folder and enter a file name.");
		GridData gd= new GridData();
		gd.widthHint= convertWidthInCharsToPixels(80);
		gd.horizontalSpan= 3;
		label.setLayoutData(gd);
	}

	private Composite createClientArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		layout.numColumns= 3;
		composite.setLayout(layout);
		return composite;
	}
	
	private void createContainerField(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Source folder : ");
		containerText = new Text(parent, SWT.BORDER);
		containerText.setText(containerName);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyContentsValid();
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				chooseContainer();
			}			
		});
		gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(15);
		browseButton.setLayoutData(gd);
	}
	
	private void createFileNameField(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Enter file name : ");
		fileNameText = new Text(parent, SWT.BORDER);
		fileNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				verifyContentsValid();
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileNameText.setLayoutData(gd);		
	}
	
	private void chooseContainer() {
		StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
		ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT); 
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(getShell(), labelProvider, provider);
		dialog.setTitle("Folder Selection");
		dialog.setMessage("Choose a folder");
		dialog.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		dialog.addFilter(createViewerFilter());
		dialog.open();
		initContainerName((IJavaElement)dialog.getFirstResult());
		containerText.setText(containerName);
	}

	private ViewerFilter createViewerFilter() {
		ViewerFilter filter= new ViewerFilter() {
			public boolean select(Viewer viewer, Object parent, Object element) {
				if (IPackageFragmentRoot.class.isInstance(element)) {
					try {
						return ((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE;
					}
					catch (JavaModelException e) {
						e.printStackTrace();
						return false;
					}
				}
				return IJavaProject.class.isInstance(element) || IJavaModel.class.isInstance(element);
			}
		};
		return filter;
	}
	
	private IJavaElement getInitialJavaElement(IStructuredSelection selection) {
		IJavaElement javaElement= null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement= selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable= (IAdaptable) selectedElement;							
				javaElement= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
				if (javaElement == null) {
					IResource resource= (IResource) adaptable.getAdapter(IResource.class);
					if (resource != null && resource.getType() != IResource.ROOT) {
						while (javaElement == null && resource.getType() != IResource.PROJECT) {
							resource= resource.getParent();
							javaElement= (IJavaElement) resource.getAdapter(IJavaElement.class);
						}
						if (javaElement == null) {
							javaElement= JavaCore.create(resource);
						}
					}
				}
			}
		}
		return javaElement;
	}
	
	private void initContainerName(IJavaElement elem) {
		IPackageFragmentRoot initRoot= null;
		IJavaElement javaElement = elem;
		while (javaElement != null && javaElement.getElementType() != IJavaElement.PACKAGE_FRAGMENT_ROOT) {
			javaElement = javaElement.getParent();
		}
		if (javaElement != null) {
			initRoot = (IPackageFragmentRoot)javaElement;
		}
		if (initRoot == null || initRoot.isArchive()) {
			initRoot = getRootForEmbeddingProject(elem);
		}	
		setCurrentRoot(initRoot);
	}
	
	private void setCurrentRoot(IPackageFragmentRoot root) {
		containerName = (root == null) ? "" : root.getPath().makeRelative().toString(); 
	}	
	
	private IPackageFragmentRoot getRootForEmbeddingProject(IJavaElement elem) {
		IPackageFragmentRoot result = null;
		IJavaProject javaProject= elem.getJavaProject();
		if (javaProject != null) {
			try {
				if (javaProject.exists()) {
					IPackageFragmentRoot[] roots= javaProject.getPackageFragmentRoots();
					for (int i= 0; i < roots.length; i++) {
						if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
							result= roots[i];
							break;
						}
					}							
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			if (result == null) {
				result= javaProject.getPackageFragmentRoot(javaProject.getResource());
			}
		}
		return result;
	}
	
	private void verifyContentsValid() {
		if (!checkContainerPathValid()) {
			setErrorMessage("The folder does not exist.");
			setPageComplete(false);
		} else if (isFileNameEmpty()) {
			setErrorMessage("Enter a file name.");
			setPageComplete(false);
		} else if (fileExists()){
			setErrorMessage("A file with this name already exists.");
			setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}
	
	private boolean fileExists() {
		return getPageFlowFile().exists();
	}
	
	private boolean isFileNameEmpty() {
		String str = fileNameText.getText();
		return str == null || "".equals(str);
	}
	
	private boolean checkContainerPathValid() {
		IPath path = new Path(containerText.getText());
		return workspaceRoot.getFolder(path).exists();
	}
	
	public String getFileName() {
		String fileName = fileNameText.getText();
		if (fileName.length() <= 4 || (fileName.length() > 4 && !".xml".equals(fileName.substring(fileName.length() - 4)))) {
			fileName = fileName + ".xml";
		}
		return fileName;
	}
	
	public IFolder getProcessFolder() {
		IPath path = new Path(containerText.getText());
		return workspaceRoot.getFolder(path);
	}
	
	public IFile getPageFlowFile() {
		return getProcessFolder().getFile(getFileName());
	}
	
}
