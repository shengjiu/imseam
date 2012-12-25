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
package org.jbpm.ui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.jbpm.ui.contributor.ElementContributor;

public class ElementType {
	
	private static Map types;
	private static Map paletteMap;
	
	private String name;
	private IConfigurationElement configElement;
	private ElementContributor contributor;
	
	private static void initializeTypes() {
		types = new HashMap();
		paletteMap = new TreeMap();
		IExtension[] extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.jbpm.ui.graphElements").getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) {
				processConfigElement(configElements[j]);
			}
		}		
	}
	
	private static void processConfigElement(IConfigurationElement configElement) {
		if (!configElement.getName().equals("graphElement")) return;
		ElementType type = new ElementType(configElement);
		addToModelMap(type);
		addToPaletteMap(type);
	}
	
	private static void addToModelMap(ElementType type) {
		type.name = type.configElement.getAttribute("name");
		types.put(type.name, type);
	}

	private static void addToPaletteMap(ElementType type) {
		IConfigurationElement[] entries = type.configElement.getChildren("entry");
		if (entries.length == 0) return;
		String entryId = entries[0].getAttribute("id");
		String categoryId = entries[0].getAttribute("category");
		Map category = (Map)paletteMap.get(categoryId);
		if (category == null) {
			category = new TreeMap();
			paletteMap.put(categoryId, category);
		}
		category.put(entryId, type);
	}
	
	public static Set getPaletteCategories() {
		return paletteMap.keySet();
	}
	
	public static Map getPaletteEntriesFor(String categoryName) {
		return (Map)paletteMap.get(categoryName);
	}
	
	public static ElementType getElementType(String name) {
		if (types == null) {
			initializeTypes();
		}
		return (ElementType)types.get(name);
	}
	
	public ElementType(IConfigurationElement configElement) {
		this.configElement = configElement;
		this.name = configElement.getAttribute("name");
	}
	
	public IConfigurationElement getConfigElement() {
		return configElement;
	}
	
	public ElementContributor getContributor() {
		if (contributor == null) {
			contributor = createContributor();
		}
		return contributor;
	}
	
	public String getName() {
		return name;
	}
	
	private ElementContributor createContributor() {
		ElementContributor result = null;
		try {
			result = (ElementContributor)configElement.createExecutableExtension("contributor");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

}
