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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jbpm.ui.SharedImages;
import org.jbpm.ui.factory.ElementCreationFactory;
import org.jbpm.ui.model.ElementType;


public class DesignerPaletteRoot extends PaletteRoot {
	
	private DesignerEditor editor;
	
	public DesignerPaletteRoot(DesignerEditor editor) {
		this.editor = editor;
		addControls();	
	}

	private void addControls() {
		List categories = new ArrayList();
		categories.add(createDefaultControls());
		Iterator iterator = ElementType.getPaletteCategories().iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			categories.add(createCategory((String)object));
		}
		addAll(categories);
	}
	
	private PaletteGroup createCategory(String categoryName) {
		PaletteGroup controls = new PaletteGroup(categoryName);
		controls.setId(categoryName);
		Map entries = ElementType.getPaletteEntriesFor(categoryName);
		Iterator iterator = entries.keySet().iterator();
		while (iterator.hasNext()) {
			PaletteEntry entry = createEntry((ElementType)entries.get(iterator.next()));
			if (entry != null) {
				controls.add(entry);
			}
		}
		return controls;
	}
	
	private PaletteEntry createEntry(ElementType elementType) {
		IConfigurationElement configElement = elementType.getConfigElement();
		String name = configElement.getAttribute("name");
		CreationFactory factory = getCreationFactory(configElement);
		IConfigurationElement info = configElement.getChildren("entry")[0];		
		String label = info.getAttribute("label");
		String tooltip = info.getAttribute("tooltip");
		String type = info.getAttribute("type");
		ImageDescriptor descriptor = getIconImageDescriptor(info); 		
		if ("node".equals(type) && !"page".equals(name)) {
			return new CreationToolEntry(label, tooltip, factory, descriptor, null);
		} else if ("connection".equals(type)) {
			return new ConnectionCreationToolEntry(label, tooltip, factory, descriptor, null);
		} else {
			return null;
		}
	}

  /**
   * Returns the descriptor of the icon which should be associated with the palette entry.
   * <p>
   * If the "icon" attribute is defined by the extension than the specified icon is used.
   * If the "icon" attribute is not specified it tries to load a default icon defined in "org.jbpm.ui.icon" folder. 
   * </p>
   *  
   * @param element Configuration element from the extension point. 
   * @return Descriptor of the icon which should be displayed in the palette. 
   */
  private ImageDescriptor getIconImageDescriptor(final IConfigurationElement element){
     String icon = element.getAttribute("icon");
      
     if(icon == null) {
       String label = element.getAttribute("label");
        
       return ImageDescriptor.createFromFile(SharedImages.class, "icon/" + label.toLowerCase() + ".gif");
     }
     else {
       String plugin = element.getDeclaringExtension().getNamespace();
       return AbstractUIPlugin.imageDescriptorFromPlugin(plugin, icon);
     }
   }
  
	private CreationFactory getCreationFactory(IConfigurationElement configElement) {
//		return ElementAdapterFactory.INSTANCE.getCreationFactory(
//				configElement.getAttribute("name"), editor);
		return new ElementCreationFactory(configElement.getAttribute("name"), editor);
	}
	
	private PaletteGroup createDefaultControls() {
		PaletteGroup controls = new PaletteGroup("Default Tools");
		controls.setId("org.jbpm.palette.DefaultTools");
		addSelectionTool(controls);
		addMarqueeTool(controls);
		return controls;
	}
	
	private void addMarqueeTool(PaletteGroup controls) {
		ToolEntry tool = new MarqueeToolEntry();
		tool.setId("org.jbpm.ui.palette.Marquee");
		controls.add(tool);
	}

	private void addSelectionTool(PaletteGroup controls) {
		ToolEntry tool = new SelectionToolEntry();
		tool.setId("org.jbpm.ui.palette.Selection");
		controls.add(tool);
		setDefaultEntry(tool);
	}

}
