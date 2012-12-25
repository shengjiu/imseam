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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.jbpm.ui.pageflow.outline.PageFlowOutlineViewer;

public class PageFlowGraphicalEditorPage extends EditorPart {
	
	private PageFlowEditor editor;
	private SashForm designerForm;
	private PageFlowPaletteViewer designerPaletteViewer;
	private PageFlowModelViewer designerModelViewer;
	private PageFlowOutlineViewer outlineViewer;

	public PageFlowGraphicalEditorPage(PageFlowEditor editor) {
		this.editor = editor;
	}
	
	public void createPartControl(Composite parent) {
		designerForm = new SashForm(parent, SWT.HORIZONTAL);
		addPaletteViewer();
		addModelViewer();
		addOutlineViewer();
		designerForm.setWeights(new int[] {15, 85});
	}
	
	private void addOutlineViewer() {
		setOutlineViewer(new PageFlowOutlineViewer(editor));
	}
	
	private void addModelViewer() {
		setDesignerModelViewer(new PageFlowModelViewer(editor));
		getDesignerModelViewer().createControl(designerForm);
	}
	
	private void addPaletteViewer() {
		setDesignerPaletteViewer(new PageFlowPaletteViewer(editor));
		getDesignerPaletteViewer().createControl(designerForm);
	}
	
	public void setFocus() {	
	}
	
	public void doSave(IProgressMonitor monitor) {
		
		SWTGraphics g = null;
		GC gc = null;
		Image image = null;
	
		LayerManager lm = (LayerManager)designerModelViewer.getEditPartRegistry().get(LayerManager.ID);
		IFigure figure = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
		
		try {
		
		    Rectangle r = figure.getBounds();
		    editor.getProcessDefinition().setDimension(new Dimension(r.width, r.height));
			image = new Image(Display.getDefault(), r.width, r.height);
	        gc = new GC(image);
	        g = new SWTGraphics(gc);
	        g.translate(r.x * -1, r.y * -1);
	        figure.paint(g);
	        ImageLoader imageLoader = new ImageLoader();
	        imageLoader.data = new ImageData[] {image.getImageData()};
	        imageLoader.save(getImageSavePath(), SWT.IMAGE_JPEG);
	        refreshProcessFolder();
	        
	    } finally {
	        if (g != null) {
	            g.dispose();
	        }
	        if (gc != null) {
	            gc.dispose();
	        }
	        if (image != null) {
	            image.dispose();
	        }
	    }
	    
	}
	
	private void refreshProcessFolder() {
		try {
			IFile file = ((FileEditorInput)getEditorInput()).getFile();
			file.getParent().refreshLocal(1, null);			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private String getImageSavePath() {
		IFile file = ((FileEditorInput)getEditorInput()).getFile();
		String name = file.getName();
		if (name.startsWith(".gpd.") && name.length() > 7) {
			name = name.substring(5, name.length() - 3) + "jpg";			
		} else {
			name = "pageflow-image.jpg";
		}
		IPath path = file.getRawLocation().removeLastSegments(1).append(name);
		return path.toOSString();
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
		initSite(site);
		initInput(input);
	}
	
	private void initSite(IEditorSite site) {
		setSite(site);
	}
	
	private void initInput(IEditorInput input) throws PartInitException {
		IEditorInput gpdInput = PageFlowContentProvider.INSTANCE.getGpdEditorInput(input);
		setInput(gpdInput);
		PageFlowContentProvider.INSTANCE.addGraphicalInfo(editor.getProcessDefinition(), gpdInput);
	}
	
	public PageFlowModelViewer getDesignerModelViewer() {
		return designerModelViewer;
	}
	

	public void setDesignerModelViewer(PageFlowModelViewer designerModelViewer) {
		this.designerModelViewer = designerModelViewer;
	}
	

	public PageFlowPaletteViewer getDesignerPaletteViewer() {
		return designerPaletteViewer;
	}
	

	public void setDesignerPaletteViewer(PageFlowPaletteViewer designerPaletteViewer) {
		this.designerPaletteViewer = designerPaletteViewer;
	}
	

	public PageFlowOutlineViewer getOutlineViewer() {
		return outlineViewer;
	}
	

	public void setOutlineViewer(PageFlowOutlineViewer outlineViewer) {
		this.outlineViewer = outlineViewer;
	}
	
	public PageFlowEditor getEditor() {
		return editor;
	}
	
}
