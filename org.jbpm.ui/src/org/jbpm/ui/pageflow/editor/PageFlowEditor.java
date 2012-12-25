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
package org.jbpm.ui.pageflow.editor;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
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

public class PageFlowEditor extends XMLMultiPageEditorPart {

	private ProcessDefinition processDefinition;

	private StructuredTextEditor sourcePage;

	private PageFlowGraphicalEditorPage graphPage;

	private CommandStackListener commandStackListener;

	private ISelectionListener selectionListener;

	private EditDomain editDomain;

	private PageFlowActionRegistry actionRegistry;
	
	private MenuManager sourceContextMenuManager;

	private boolean isDirty = false;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		FileEditorInput fileInput = (FileEditorInput) input;
		if (fileInput.getName().equals(".jbpm-pageflow.gpi")) {
			fileInput = PageFlowContentProvider.INSTANCE
					.getJpdlEditorInput(fileInput);
		}
		super.init(site, fileInput);
		initActionRegistry();
		initEditDomain();
		initCommandStackListener();
		initSelectionListener();
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
		actionRegistry = new PageFlowActionRegistry(this);
	}

	protected void createPages() {
		try {
			super.createPages();
			initSourcePage();
			addGraphPage();
			setActivePage(0);
		} catch (PartInitException e) {
			DesignerLogger.logError("Could not create graphical viewer", e);
		}
	}

	public Object getAdapter(Class adapter) {
		Object result = null;
		if (adapter == PageFlowEditor.class) {
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
		graphPage = new PageFlowGraphicalEditorPage(this);
		addPage(0, graphPage, getEditorInput());
		setPageText(0, "Diagram");
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

	public PageFlowModelViewer getGraphicalViewer() {
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

	PageFlowActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public boolean isDirty() {
		return isDirty || super.isDirty();
	}

	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		graphPage.doSave(monitor);
		PageFlowContentProvider.INSTANCE.saveToInput(getEditorInput(),
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

}
