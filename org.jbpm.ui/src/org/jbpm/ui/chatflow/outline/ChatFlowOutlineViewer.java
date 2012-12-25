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
package org.jbpm.ui.chatflow.outline;

import java.util.Iterator;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.PageBook;
import org.jbpm.ui.DesignerPlugin;
import org.jbpm.ui.chatflow.editor.ChatFlowEditor;
import org.jbpm.ui.chatflow.editor.ChatFlowModelViewer;
import org.jbpm.ui.model.GraphElement;
import org.jbpm.ui.part.tree.OutlineRootTreeEditPart;

public class ChatFlowOutlineViewer extends ContentOutlinePage {
	
	private PageBook pageBook;
	private Control treeview;
	private Canvas overview;
	private ChatFlowEditor editor;
	private Thumbnail thumbnail;
	private IAction showOverviewAction;
	private IAction showTreeviewAction;
	private ISelectionListener selectionListener;
	
	public ChatFlowOutlineViewer(ChatFlowEditor editor) {
		super(new TreeViewer());
		this.editor = editor;
	}

	public void createControl(Composite parent) {
		createToolBar();
		createPageBook(parent);
		createSelectionListener();
	}
	
	private void createSelectionListener() {
		selectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				changeSelection(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(selectionListener);
	}
	
	private void changeSelection(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection structuredSelection = (IStructuredSelection)selection;
		Iterator iterator = structuredSelection.iterator();
		while (iterator.hasNext()) {
			Object selectedObject = iterator.next();
			if (!(selectedObject instanceof EditPart)) continue;
			EditPart source = (EditPart)selectedObject;
			EditPart target = (EditPart)getViewer().getEditPartRegistry().get(source.getModel());
			if (target != null) {
				getViewer().select(target);
			}
		}
	}
	
	private void createToolBar() {
		IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		createShowOverviewAction(tbm);		
		createShowTreeviewAction(tbm);
	}

	private void createShowOverviewAction(IToolBarManager tbm) {
		showOverviewAction = new Action() {
			public void run() {
				showOverview();
			}
		};
		showOverviewAction.setImageDescriptor(
				ImageDescriptor.createFromFile(
						DesignerPlugin.class, "icon/overview.gif"));
		tbm.add(showOverviewAction);
	}
	
	private void showOverview() {
		showTreeviewAction.setChecked(false);
		showOverviewAction.setChecked(true);
		pageBook.showPage(overview);
		thumbnail.setVisible(true);
	}
	
	private void createShowTreeviewAction(IToolBarManager tbm) {
		showTreeviewAction = new Action() {
			public void run() {
				showTreeview();
			}
		};
		showTreeviewAction.setImageDescriptor(
				ImageDescriptor.createFromFile(
						DesignerPlugin.class, "icon/treeview.gif"));
		tbm.add(showTreeviewAction);
	}
	
	private void showTreeview() {
		showTreeviewAction.setChecked(true);
		showOverviewAction.setChecked(false);
		pageBook.showPage(treeview);
		thumbnail.setVisible(false);
	}
	
	private void createPageBook(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);
		createTreeview(pageBook);
		createOverview(pageBook);
		showTreeview();
	}
	
	private ScalableFreeformRootEditPart getModelViewerRootEditPart() {
		return (ScalableFreeformRootEditPart)getModelViewer().getRootEditPart();
	}
	
	private ChatFlowModelViewer getModelViewer() {
		return editor.getGraphicalViewer();
//		return null;
	}

	private void createOverview(Composite parent) {
		ScalableFreeformRootEditPart rootEditPart = getModelViewerRootEditPart();
		overview = new Canvas(parent, SWT.NONE);
		LightweightSystem lws = new LightweightSystem(overview);
		thumbnail = new ScrollableThumbnail((Viewport)rootEditPart.getFigure());
		thumbnail.setBorder(new MarginBorder(3));
		thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
		lws.setContents(thumbnail);
	}
	
	private void createTreeview(Composite parent) {
		treeview = getViewer().createControl(parent);
		treeview.setMenu(createContextMenu());
		treeview.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				handleDoubleClick();				
			}
		});
		getSite().setSelectionProvider(getViewer());
		getViewer().setEditDomain(getModelViewer().getEditDomain());
		getViewer().setEditPartFactory(new EditPartFactory() {
			public EditPart createEditPart(EditPart context, Object model) {
				if (model instanceof ChatFlowOutlineContent) {
					return new OutlineRootTreeEditPart(model);
				}
				if (!(model instanceof GraphElement)) return null;
				GraphElement element = (GraphElement)model;
				return element.getElementType().getContributor().createTreeEditPart(element);
			}

		}); 
		setContents(new ChatFlowOutlineContent(editor.getProcessDefinition()));
	}
	
	private void handleDoubleClick() {
		EditPart editPart = (EditPart)getViewer().getSelectedEditParts().get(0);
		Request openPropertiesRequest = new Request(RequestConstants.REQ_OPEN);
		editPart.performRequest(openPropertiesRequest);
	}

	private Menu createContextMenu() {
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				fillContextMenu(m);
			}
		});
		Menu result = menuManager.createContextMenu(treeview);
		getSite().registerContextMenu("org.jbpm.ui.outline.context", menuManager, getViewer());
		return result; 
	}
	
	private void fillContextMenu(IMenuManager menuManager) {
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	public CommandStack getCommandStack() {
		return editor.getCommandStack();
	}

	public Control getControl() {
		return pageBook;
	}

	public void setFocus() {
		if (getControl() != null) {
			getControl().setFocus();
		}
	}

	public void setContents(Object contents) {
		getViewer().setContents(contents);
	}

	public void dispose() {
		if (null != thumbnail) {
			thumbnail.deactivate();
		}
		super.dispose();
	}


}
