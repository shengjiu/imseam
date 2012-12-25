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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Swimlane extends GraphElement {
	
	String expression;
	String handlerClassName;
	String configurationType;
	
	public String getName() {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
		return node == null ? null : node.getNodeValue();
	}
	
	public void setName(String name) {
		if (name != null && !name.equals(getName()) && canSetNameTo(name)) {
			uncheckedSetName(name);
		}		
		notifyChange(ELEMENT_NAME_SET);
	}
		
	private boolean canSetNameTo(String name) {
		ProcessDefinition processDefinition = getProcessDefinition();
		if (processDefinition == null) return false;
		Swimlane swimlane = processDefinition.getSwimlaneByName(name);
		return swimlane == null || swimlane == this; 
	}
	
	private void uncheckedSetName(String name) {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
		if (node == null) {
			node = getNode().getOwnerDocument().createAttribute("name");
			getNode().getAttributes().setNamedItem(node);
		}
		node.setNodeValue(name);
		
	}
	
	public org.w3c.dom.Node getAssignment() {
		return getNode("assignment");		
	}
	
	public org.w3c.dom.Node getNode(String name) {
		if (name == null) return null;
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			org.w3c.dom.Node candidate = list.item(i);
			if (name.equals(candidate.getNodeName())){
				return candidate;
			}
		}
		return null;		
	}
	
	public String getAssignmentExpression() {
		return getNodeExpression("assignment");
	}
	
	private String getNodeExpression(String nodeName) {
		org.w3c.dom.Node node = getNode(nodeName);
		if (node == null) return null;
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem("expression");
		return attr == null ? null : attr.getNodeValue();
	}
	
	private void setNodeExpression(String nodeName, String expression) {
		org.w3c.dom.Node node = getNode(nodeName);
		if (node == null) return;
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem("expression");
		if (attr == null) {
			attr = node.getOwnerDocument().createAttribute("expression");
			node.getAttributes().setNamedItem(attr);
		}
		if (expression == null) {
			node.getAttributes().removeNamedItem("expression");
		} else {
			attr.setNodeValue(expression);
		}
	}
	
	public void setAssignmentExpression(String expression) {
		setNodeExpression("assignment", expression);
	}
	
	public void addAssignment() {
		if (hasAssignment()) return;
		addNode("assignment");
	}
	
	public boolean hasAssignment() {
		return (getAssignment() != null);
	}
	
	public void addNode(String name) {
		IDOMNode last = (IDOMNode)getNode().getLastChild();
		if (last == null) {
			last = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
			getNode().appendChild(last);
		}
		addNodeBefore(last, name);
	}
	
	private void addNodeBefore(IDOMNode before, String name) {
		IDOMNode node = (IDOMNode)getNode().getOwnerDocument().createElement(name);
		getNode().insertBefore(node, before);
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, node);
	}
	
	public void removeAssignment() {
		removeNode("assignment");
	}
	
	private void removeNode(String name) {
		IDOMNode node = (IDOMNode)getNode(name);
		if (node == null) return;
		IDOMNode previous = (IDOMNode)node.getPreviousSibling();
		if (previous != null) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(node);
	}
	
	private void setNodeDelegateClassName(String nodeName, String delegateClassName) {
		org.w3c.dom.Node node = getNode(nodeName);
		if (node == null) return;
		String oldName = getNodeDelegateClassName(nodeName);
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem("class");
		if (attr == null) {
			attr = node.getOwnerDocument().createAttribute("class");
			node.getAttributes().setNamedItem(attr);
		}
		if (delegateClassName == null) {
			node.getAttributes().removeNamedItem("class");
		} else {
			attr.setNodeValue(delegateClassName);
		}
		getProcessDefinition().classNameChanged(oldName, delegateClassName);
	}
	
	private String getNodeDelegateClassName(String nodeName) {
		org.w3c.dom.Node node = getNode(nodeName);
		if (node == null) return null;
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem("class");
		return attr == null ? null : attr.getNodeValue();
	}
	
	public String getAssignmentDelegateClassName() {
		return getNodeDelegateClassName("assignment");
	}
	
	public void setAssignmentDelegateClassName(String delegateClassName) {
		setNodeDelegateClassName("assignment", delegateClassName);
	}
	
	private String getNodeConfigurationType(String nodeName) {
		org.w3c.dom.Node node = getNode(nodeName);
		if (node == null) return null;
		org.w3c.dom.Node attr  = node.getAttributes().getNamedItem("config-type");
		return attr == null ? "field" : attr.getNodeValue();
	}
	
	public String getAssignmentConfigurationType() {
		return getNodeConfigurationType("assignment");
	}
	
	private void setNodeConfigurationType(String nodeName, String configurationType) {
		org.w3c.dom.Node node = getNode(nodeName);
		if (node == null) return;
		org.w3c.dom.Node attr = node.getAttributes().getNamedItem("config-type");
		if (attr == null) {
			attr = node.getOwnerDocument().createAttribute("config-type");
			node.getAttributes().setNamedItem(attr);
		}
		if (configurationType == null || "field".equals(configurationType)) {
			node.getAttributes().removeNamedItem("config-type");
		}
		attr.setNodeValue(configurationType);		
	}
	
	public void setAssignmentConfigurationType(String configurationType) {
		if (getAssignmentConfigurationType().equals(configurationType)) return;
		removeChildren(getNode("assignment"));
		setNodeConfigurationType("assignment", configurationType);
	}
	
	private void setNodeConfigurationString(String nodeName, String info) {	
		org.w3c.dom.Node node = getNode(nodeName);		
		if (node == null) return;
		removeChildren(node);
		if (info != null) {
			StringBuffer nodeText = new StringBuffer("\n");
			if (info.trim().indexOf('\n') == -1) {
				nodeText.append(getPaddingString(getLevel() + 1));
			}
			nodeText.append(info.trim()).append("\n").append(getPaddingString(getLevel()));
			IDOMNode config = (IDOMNode)getNode().getOwnerDocument().createTextNode(nodeText.toString());
			node.appendChild(config);
		}
	}
	
	public void setAssignmentConfigurationString(String info) {	
		setNodeConfigurationString("assignment", info);
	}
	
	private String getNodeConfigurationString(String nodeName) {
		if (getNode(nodeName).hasChildNodes()) {
			return ((IDOMNode)getNode(nodeName).getFirstChild()).getSource().trim();
		}
		return null;
	}
	
	public String getAssignmentConfigurationString() {
		return getNodeConfigurationString("assignment");
	}
	
	private void removeChildren(org.w3c.dom.Node node) {
		while (node.getChildNodes().getLength() > 0) {
			node.removeChild(node.getFirstChild());
		}
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
			new TextPropertyDescriptor("name", "Name")		};
	}

	public Object getPropertyValue(Object id) {
		if ("name".equals(id)) {
			return getName() == null ? "" : getName();
		}
		return null;
	}

	public void setPropertyValue(Object id, Object value) {
		if ("name".equals(id)) {
			setName((String)value);
		}
	}
	
	public String getAssignmentConfigurationInfo(String key) {
		return getNodeConfigurationInfo("assignment", key);
	}
	
	public void setAssignmentConfigurationInfo(String key, String value) {
		setNodeConfigurationInfo("assignment", key, value);
	}
	
	public String getNodeConfigurationInfo(String nodeName, String key) {
		Node node = getNode(nodeName);
		if (node == null) return null;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeName().equals(key)) {
				return child.getFirstChild() != null ? child.getFirstChild().getNodeValue() : "";
			}
		}
		return null;
	}
	
	public void setNodeConfigurationInfo(String nodeName, String key, String value) {
		Node node = getNode(nodeName);
		if (node == null) return;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeName().equals(key)) {
				Node first = child.getFirstChild();
				if (first == null) {
					first = child.getOwnerDocument().createTextNode(value);
					child.appendChild(first);
				} else {
					first.setNodeValue(value);
				}
			}
		}
	}
	
	public void addAssignmentConfigurationInfo(String key, String value) {
		addNodeConfigurationInfo("assignment", key, value);
	}
	
	private void addNodeConfigurationInfo(String nodeName, String key, String value) {
		Node node = getNode(nodeName);		
		if (node == null) return;
		IDOMNode before = (IDOMNode)node.getLastChild();
		if (before == null) {
			before = (IDOMNode)getNode().getOwnerDocument().createTextNode(
					"\n" + getPaddingString(getLevel()));
			node.appendChild(before);
		}
		addNodeConfigurationInfoBefore(nodeName, key, value, before);
	}
	
	private void addNodeConfigurationInfoBefore(String nodeName, String key, String value, IDOMNode before) {
		Document document = getNode(nodeName).getOwnerDocument();
		IDOMNode element = (IDOMNode)document.createElement(key);
		getNode(nodeName).insertBefore(element, before);
		element.appendChild((IDOMNode)document.createTextNode(value));
		IDOMNode text = (IDOMNode)document.createTextNode("\n" + getPaddingString(getLevel() + 1));
		getNode(nodeName).insertBefore(text, element);
	}
	
	public void removeAssignmentConfigurationInfo(String key) {
		removeNodeConfigurationInfo("assignment", key);
	}
	
	private void removeNodeConfigurationInfo(String nodeName, String key) {
		Node node = getNodeChildNamed(nodeName, key);
		if (node == null) return;
		Node before = node.getPreviousSibling();
		getNode(nodeName).removeChild(node);
		if (before != null && before.getNodeValue().trim().equals("")) {
			getNode(nodeName).removeChild(before);
		}	
		removeLastChildIfEmpty(nodeName);
	}
	
	private void removeLastChildIfEmpty(String nodeName) {
		Node node = getNode(nodeName);
		if (node.getChildNodes().getLength() == 1) {
			Node child = node.getFirstChild();
			if (child.getNodeValue().trim().equals("")) {
				node.removeChild(child);
			}
		}
	}
	
	private Node getNodeChildNamed(String nodeName, String childName) {
		Node node = getNode(nodeName);
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(childName)) return children.item(i);
		}
		return null;
	}

}
