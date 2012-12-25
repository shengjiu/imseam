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
import java.util.EventObject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.INestableKeyBindingService;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.jbpm.ui.DesignerLogger;
import org.jbpm.ui.factory.ElementAdapterFactory;
import org.jbpm.ui.model.ProcessDefinition;
import org.w3c.dom.Document;

public class DesignerEditor extends XMLMultiPageEditorPart {

	private ProcessDefinition processDefinition;

	private StructuredTextEditor sourcePage;

	private DesignerGraphicalEditorPage graphPage;

	private DesignerSwimlaneEditorPage swimlanePage;

	private DesignerDeploymentEditorPage deploymentPage;

	private CommandStackListener commandStackListener;

	private ISelectionListener selectionListener;

	private EditDomain editDomain;

	private DesignerActionRegistry actionRegistry;
	
	private MenuManager sourceContextMenuManager;

	private boolean isDirty = false;
	
	private IResourceChangeListener resourceChangeListener;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		FileEditorInput fileInput = (FileEditorInput) input;
		if (fileInput.getName().equals("gpd.xml") || fileInput.getName().indexOf(".gpd.") == 0) {
			fileInput = DesignerContentProvider.INSTANCE
					.getJpdlEditorInput(fileInput);
		}
		super.init(site, fileInput);
		initActionRegistry();
		initEditDomain();
		initCommandStackListener();
		initSelectionListener();
		initResourceChangeListener();
		initPartName();
	}
	
	private void initResourceChangeListener() {
		resourceChangeListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				handleResourceChange(event);
			}
		};
		getWorkspace().addResourceChangeListener(resourceChangeListener);
	}
	

	private void handleResourceChange(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			IFile file = ((IFileEditorInput)getEditorInput()).getFile();
			if (!file.exists()) {
				deleteProcessFolder(file);
			} else {
				ArrayList objectsToRefresh = new ArrayList();
				if (refreshDeploymentPageIfNeeded(event.getDelta(), objectsToRefresh)) {
					deploymentPage.refreshForm(objectsToRefresh);
				}
			}
		} 
	}
	
	private boolean refreshDeploymentPageIfNeeded(IResourceDelta delta, ArrayList objectsToRefresh) {
		boolean refreshNeeded = false;
		IProject project = ((IFileEditorInput)getEditorInput()).getFile().getProject();
		if (delta.getFullPath().toString().indexOf(project.getFullPath().toString()) == 0) {
			refreshNeeded = true;
			if (delta.getKind() == IResourceDelta.ADDED ) {
				objectsToRefresh.add(delta.getFullPath());
			}
		} 
		IResourceDelta[] children = delta.getAffectedChildren();
		for (int i = 0; i < children.length; i++) {
			refreshNeeded |= refreshDeploymentPageIfNeeded(children[i], objectsToRefresh);
		}
		return refreshNeeded;
	}

	private void deleteProcessFolder(IFile file) {
		final IContainer processFolder = getWorkspace().getRoot().getFolder(file.getFullPath().removeLastSegments(1));
		if (processFolder != null && processFolder.exists()) {
			WorkspaceJob job = new WorkspaceJob("delete") {
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					processFolder.delete(true, null);
					return Status.OK_STATUS;
				}
			};
			job.setRule(getWorkspace().getRuleFactory().deleteRule(processFolder));
			job.schedule();
		}
	}
	
	private IWorkspace getWorkspace() {
		return ((IFileEditorInput)getEditorInput()).getFile().getWorkspace();
	}
	
	private void initPartName() {
		FileEditorInput fileInput = (FileEditorInput) getEditorInput();
		IPath path = fileInput.getPath().removeLastSegments(1);
		path = path.removeFirstSegments(path.segmentCount() - 1);
		setPartName(path.lastSegment());
	}

	private void initSelectionListener() {
		selectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				getActionRegistry().updateEditPartActions();
			}
		};
		ISelectionService selectionService = getSite().getWorkbenchWindow()
				.getSelectionService();
		selectionService.addSelectionListener(selectionListener);
	}

	private void initCommandStackListener() {
		commandStackListener = new CommandStackListener() {
			public void commandStackChanged(EventObject event) {
				handleCommandStackChanged();
			}
		};
		getCommandStack().addCommandStackListener(commandStackListener);
	}

	private void handleCommandStackChanged() {
		getActionRegistry().updateStackActions();
		if (!isDirty() && getCommandStack().isDirty()) {
			isDirty = true;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	private void initEditDomain() {
		editDomain = new DefaultEditDomain(this);
	}

	private void initActionRegistry() {
		actionRegistry = new DesignerActionRegistry(this);
	}

	protected void createPages() {
		try {
			super.createPages();
			initSourcePage();
			addGraphPage();
			addSwimlanePage();
			addDeploymentPage();
			setActivePage(0);
		} catch (PartInitException e) {
			DesignerLogger.logError("Could not create graphical viewer", e);
		}
	}

	public Object getAdapter(Class adapter) {
		Object result = null;
		if (adapter == DesignerEditor.class) {
			result = this;
		} else if (adapter == CommandStack.class) {
			result = getCommandStack();
		} else if (adapter == IContentOutlinePage.class) {
			return getOutlineViewer();
		} else if (adapter == IPropertySheetPage.class) {
			return null;
		} else if (adapter == GraphicalViewer.class) {
			return getGraphicalViewer();
		} else {
			result = super.getAdapter(adapter);
		}
		return result;
	}

	private void addGraphPage() throws PartInitException {
		graphPage = new DesignerGraphicalEditorPage(this);
		addPage(0, graphPage, getEditorInput());
		setPageText(0, "Diagram");
	}

	private void addSwimlanePage() throws PartInitException {
		swimlanePage = new DesignerSwimlaneEditorPage(this);
		addPage(1, swimlanePage, getEditorInput());
		setPageText(1, "Swimlanes");
	}

	private void addDeploymentPage() throws PartInitException {
		deploymentPage = new DesignerDeploymentEditorPage(this);
		addPage(2, deploymentPage, getEditorInput());
		setPageText(2, "Deployment");
	}

	private ProcessDefinition getProcessDefinition(
			StructuredTextEditor sourcePage) {
		INodeNotifier node = getDocumentElement(sourcePage);
		return (ProcessDefinition) ElementAdapterFactory.INSTANCE.adapt(node);
	}

	private INodeNotifier getDocumentElement(StructuredTextEditor sourcePage) {
		INodeNotifier result = null;
		IDOMDocument document = (IDOMDocument) sourcePage
				.getAdapter(Document.class);
		if (document != null) {
			result = (INodeNotifier) document.getDocumentElement();
		}
		return result;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public DesignerModelViewer getGraphicalViewer() {
		return graphPage.getDesignerModelViewer();
	}

	private IContentOutlinePage getOutlineViewer() {
		return graphPage.getOutlineViewer();
	}

	public CommandStack getCommandStack() {
		return editDomain.getCommandStack();
	}

	public EditDomain getEditDomain() {
		return editDomain;
	}

	public EditorPart getGraphPage() {
		return graphPage;
	}

	public DesignerActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public boolean isDirty() {
		return isDirty || super.isDirty();
	}

	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		graphPage.doSave(monitor);
		DesignerContentProvider.INSTANCE.saveToInput(getEditorInput(),
				getProcessDefinition());
		getCommandStack().markSaveLocation();
		isDirty = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	protected void pageChange(int newPageIndex) {
		if (newPageIndex == 0) {
			IKeyBindingService service = getSite().getKeyBindingService();
			if (service instanceof INestableKeyBindingService) {
				INestableKeyBindingService nestableService = (INestableKeyBindingService) service;
				nestableService.activateKeyBindingService(null);
			}
		}
		super.pageChange(newPageIndex);
	}

	private void initSourcePage() {
		int pageCount = getPageCount();
		for (int i = 0; i < pageCount; i++) {
			if (getEditor(i) instanceof StructuredTextEditor) {
				sourcePage = (StructuredTextEditor) getEditor(i);
				processDefinition = getProcessDefinition(sourcePage);
			}
		}
		if (sourcePage != null) {
			initSourcePageContextMenu(sourcePage.getTextViewer()
					.getTextWidget());
		}
	}

	private void initSourcePageContextMenu(final Control control) {
		sourceContextMenuManager = new MenuManager("#PopupMenu");
		sourceContextMenuManager.setRemoveAllWhenShown(true);
		sourceContextMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				fillContextMenu(m);
			}
		});
		Menu menu = sourceContextMenuManager.createContextMenu(control);
		getSite().registerContextMenu(
				"org.jbpm.ui.editor.DesignerEditor.SourcePopupMenu",
				sourceContextMenuManager, getSite().getSelectionProvider());

		control.setMenu(menu);
	}

	private void fillContextMenu(final IMenuManager menuManager) {
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	public void dispose() {
		getWorkspace().removeResourceChangeListener(resourceChangeListener);
		super.dispose();
	}

}
