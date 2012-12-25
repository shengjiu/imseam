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

import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.properties.ClassPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Action extends GraphElement {
	
	public String getName() {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
		return node == null ? null : node.getNodeValue();
	}
	
	public void setName(String name) {
		if (name != null && canSetNameTo(name)) {
			uncheckedSetName(name);
		}		
		notifyChange(ELEMENT_NAME_SET);
	}
	
	private void uncheckedSetName(String name) {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
		if (node == null) {
			node = getNode().getOwnerDocument().createAttribute("name");
			getNode().getAttributes().setNamedItem(node);
		}
		node.setNodeValue(name);		
	}
	
	private boolean canSetNameTo(String name) {
		IDOMNode parent = (IDOMNode)getNode().getParentNode();
		if (parent == null) return true;
		GraphElement graphElement = (GraphElement)parent.getAdapterFor(GraphElement.class);
		if (graphElement == null) return true;
		NodeList list = graphElement.getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			IDOMNode node = (IDOMNode)list.item(i);
			INodeAdapter adapter = node.getAdapterFor(GraphElement.class);
			if (adapter != null && adapter instanceof Action) {
				if (name.equals(((Action)adapter).getName())) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public String getDelegateClassName() {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("class");
		return node == null ? null : node.getNodeValue();
	}
	
	public void setDelegateClassName(String delegateClassName) {
		String oldName = getDelegateClassName();
		org.w3c.dom.Node attr = getNode().getAttributes().getNamedItem("class");
		if (attr == null) {
			attr = getNode().getOwnerDocument().createAttribute("class");
			getNode().getAttributes().setNamedItem(attr);
		}
		attr.setNodeValue(delegateClassName);
		getProcessDefinition().classNameChanged(oldName, delegateClassName);
	}
	
	public String getConfigurationType() {
		org.w3c.dom.Node node  = getNode().getAttributes().getNamedItem("config-type");
		return node == null ? "field" : node.getNodeValue();
	}
	
	public void setConfigurationType(String configurationType) {
		org.w3c.dom.Node attr = getNode().getAttributes().getNamedItem("config-type");
		if (attr == null) {
			attr = getNode().getOwnerDocument().createAttribute("config-type");
			getNode().getAttributes().setNamedItem(attr);
		}
		if (configurationType == null || "field".equals(configurationType)) {
			getNode().getAttributes().removeNamedItem("config-type");
		}
		attr.setNodeValue(configurationType);		
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
			new TextPropertyDescriptor("name", "Name"),
			new ClassPropertyDescriptor("class", "Class", "org.jbpm.graph.def.ActionHandler")
		};
	}
	
	public Object getPropertyValue(Object id) {
		if ("class".equals(id)) {
			return getDelegateClassName();
		}
		return super.getPropertyValue(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if ("name".equals(id)) {
			setName((String)value);
		}
		else if ("class".equals(id)) {
			setDelegateClassName((String)value);
		}
	}
	
	public Object getConfigurationInfo() {
		String configurationType = getConfigurationType();
		if ("constructor".equals(configurationType) || "config-type".equals(configurationType)) {
			return getConfigurationString();
		} else {
			return getConfigurationMap();
		}
	}
	
	private String getConfigurationString() {
		if (getNode().hasChildNodes()) {
			return ((IDOMNode)getNode().getFirstChild()).getSource().trim();
		}
		return null;
	}
	
	private TreeMap getConfigurationMap() {
		TreeMap result = new TreeMap();
		NodeList children = getNode().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			IDOMNode node = (IDOMNode)children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				IDOMNode child = (IDOMNode)node.getFirstChild();
				result.put(node.getNodeName(), child == null ? null : child.getNodeValue());
			}
		}
		return result;
	}
	
	public void addConfigurationInfo(Object info) {
		removeChildren();
		if (info instanceof TreeMap) {
			addConfigurationInfo((TreeMap)info);
		} else {
			addConfigurationInfo((String)info);
		}
	}

	private void addConfigurationInfo(TreeMap info) {
		int level = getLevel();
		if (!info.isEmpty()) {
			IDOMNode last = (IDOMNode)getNode().getOwnerDocument().createTextNode(
					"\n" + getPaddingString(level - 1));
			getNode().appendChild(last);			
		}
		Iterator iterator = info.keySet().iterator();
		while (iterator.hasNext()) {
			IDOMNode before = (IDOMNode)getNode().getLastChild();
			String elementName = (String)iterator.next();
			String elementValue = (String)info.get(elementName);
			addConfigurationElement(elementName, elementValue, before);
		}
	}
	
	private void removeChildren() {
		NodeList list = getNode().getChildNodes();
		if (list == null) return;
		while (list.getLength() > 0) {
			getNode().removeChild((IDOMNode)list.item(0));
		}
	}
	
	public void addConfigurationElement(String name, String value, IDOMNode before) {
		Document document =getNode().getOwnerDocument();
		IDOMNode element = (IDOMNode)document.createElement(name);
		getNode().insertBefore(element, before);
		element.appendChild((IDOMNode)document.createTextNode(value));
		IDOMNode text = (IDOMNode)document.createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, element);
	}
	
	private void addConfigurationInfo(String info) {
		if (info != null) {
			StringBuffer nodeText = new StringBuffer("\n");
			if (info.trim().indexOf('\n') == -1) {
				nodeText.append(getPaddingString(getLevel()));
			}
			nodeText.append(info.trim()).append("\n").append(getPaddingString(getLevel() - 1));
			IDOMNode config = (IDOMNode)getNode().getOwnerDocument().createTextNode(nodeText.toString());
			getNode().appendChild(config);
		}
	}
	
}
