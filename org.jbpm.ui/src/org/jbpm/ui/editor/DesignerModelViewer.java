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

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.model.Element;


public class DesignerModelViewer extends ScrollingGraphicalViewer {
	
	private PropertySheetPage propertySheetPage;
	private ISelectionListener selectionListener;
	private DesignerEditor editor;
	
	public DesignerModelViewer(DesignerEditor editor) {
		this.editor = editor;
		setKeyHandler(new GraphicalViewerKeyHandler(this));
		setRootEditPart(new ScalableFreeformRootEditPart());
		prepareGrid();
	}

	private void prepareGrid() {
		getLayerManager().getLayer(LayerConstants.GRID_LAYER).setForegroundColor(PluginConstants.veryLightBlue);
		editor.getActionRegistry().registerAction(new ToggleGridAction(this));
	}
	
	public void createControl(SashForm parent) {
		initControl(parent);
		initEditDomain(parent);
		initSite(parent);
		initEditPartFactory(parent);
		initContents(parent);
		initPropertySheetPage(parent);
	}
	
	private void initControl(SashForm parent) {
		super.createControl(parent);
		getControl().setBackground(ColorConstants.white);
		// TODO create a context meny whithout the nasty 'run as' and 'debug as' entries
		ContextMenuProvider provider = new DesignerContextMenuProvider(this, editor.getActionRegistry());
		setContextMenu(provider);
		editor.getSite().registerContextMenu("org.jbpm.ui.editor.modelviewer.context", provider, this);
	}
	
//	private MenuManager createContextMenu() {
//		MenuManager menuManager = new MenuManager("#PopupMenu");
//		menuManager.setRemoveAllWhenShown(true);
//		menuManager.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager m) {
//				fillContextMenu(m);
//			}
//		});
//		return menuManager; 
//	}
//	
//	private void fillContextMenu(IMenuManager menuManager) {
//		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//		System.out.println("tata");
//	}
	private void initEditDomain(SashForm parent) {
		EditDomain editDomain = editor.getEditDomain();
		editDomain.addViewer(this);
	}
	
	private void initSite(SashForm parent) {
		IWorkbenchPartSite site = editor.getGraphPage().getSite();
		site.setSelectionProvider(this);
		selectionListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part, ISelection sel) {
				changeSelection(part, sel);
			}
		};
		site.getPage().addPostSelectionListener(selectionListener);
	}
	
	private void changeSelection(IWorkbenchPart part, ISelection sel) {
		if (!(sel instanceof IStructuredSelection)) return;
		IStructuredSelection structuredSelection = (IStructuredSelection)sel;
		Iterator iterator = structuredSelection.iterator();
		while (iterator.hasNext()) {
			Object selectedObject = iterator.next();
			if (selectedObject == null || !(selectedObject instanceof EditPart)) continue;
			EditPart editPart = (EditPart)selectedObject;
			EditPart objectToSelect = (EditPart)getEditPartRegistry().get(editPart.getModel());
			if (objectToSelect != null) select(objectToSelect);
		}
	}
	
	private void initEditPartFactory(SashForm parent) {
		setEditPartFactory(new EditPartFactory() {
			public EditPart createEditPart(EditPart context, Object object) {
				if (!(object instanceof Element)) return null;
				Element element = (Element)object;
				return element.getElementType().getContributor().createGraphicalEditPart(element);
			}			
		});
	}
	
	private void initContents(SashForm parent) {
		setContents(editor.getProcessDefinition());
	}
	
	private void initPropertySheetPage(SashForm parent) {
		CommandStack commandStack = editor.getCommandStack();
		IPropertySheetEntry rootEntry = new UndoablePropertySheetEntry(commandStack);
		setPropertySheetPage(new PropertySheetPage());
		getPropertySheetPage().setRootEntry(rootEntry);
	}
	
	private void setPropertySheetPage(PropertySheetPage propertySheetPage) {
		this.propertySheetPage = propertySheetPage;
	}
	
	PropertySheetPage getPropertySheetPage() {
		return propertySheetPage;
	}
	
	public Dimension getDimension() {
		Rectangle rectangle = getControl().getBounds();
		return new Dimension(rectangle.width, rectangle.height);
	}
	
	public FigureCanvas getFigureCanvas() {
		return super.getFigureCanvas();
	}
	
}
