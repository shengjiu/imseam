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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Transition extends GraphElement  {

	private List bendpoints;
    private TransitionLabel label; 
 	
	
	public Transition() {
		bendpoints = new ArrayList();
	}
	
	public TransitionLabel getLabel() {
		if (label == null) {
			label = new TransitionLabel();
		}
		return label;
	}
	
	public String getNextActionName(String eventType) {
		return getNextActionName();
	}
	
	public String getNextActionName() {
		int runner = 1;
		while (true) {
			String candidate = "action" + runner;
			if (getActionByName(candidate) == null) {
				return candidate;
			}
			runner ++;
		}				
	}
		
	public void notifyChange(int messageId) {
		if (messageId == TRANSITION_TARGET_CHANGED) {
			Node target = getTarget();
			if (target != null ) {
				target.notifyChange(NODE_ARRIVING_TRANSITION_ADDED);
			}
		}
		super.notifyChange(messageId);
	}
	
	public Action getActionByName(String name) {
		List actions = getActions();
		for (int i = 0; i < actions.size(); i++) {
			if (name.equals(((Action)actions.get(i)).getName())) {
				return (Action)actions.get(i);
			}
		}
		return null;
	}
	
	public void addAction(String eventType, Action action) {
		IDOMNode last = (IDOMNode)getNode().getLastChild();
		if (last == null) {
			last = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n      ");
			getNode().appendChild(last);
		}
		addActionBefore(action, last);
	}
	
	public void addActionBefore(Action action, IDOMNode before) {
		if (before == null) {
			getNode().appendChild(action.getNode());
		} else {
			getNode().insertBefore(action.getNode(), before);
		}
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n         ");
		getNode().insertBefore(text, action.getNode());
		add(action);
	}
	
	public void add(GraphElement adapter) {
		if (adapter instanceof Action) {
			notifyChange(ELEMENT_ACTION_ADDED);
		}
	}
	
	public void removeAction(String eventType, Action action) {
		IDOMNode previous = (IDOMNode)action.getNode().getPreviousSibling();
		if (previous != null && previous instanceof Text) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(action.getNode());
		remove(action);
	}
	
	public void remove(GraphElement adapter) {
		if (adapter instanceof Action) {
			notifyChange(ELEMENT_ACTION_REMOVED);
		}
	}
	
	public List getActions() {
		ArrayList result = new ArrayList();
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Action) {
				result.add(adapter);
			}
		}
		return result;
	}
	
	public List getBendpoints() {
		if ((getSource() == getTarget()) && (bendpoints.isEmpty())) {
			bendpoints = createBendPointsForSelfReference();
		}
		return bendpoints;
	}
	
	private Bendpoint createBendpoint(int xStart, int yStart, int xEnd, int yEnd) {
		Bendpoint bendpoint = new Bendpoint();
		Dimension xDimension = new Dimension(xStart, yStart);
		Dimension yDimension = new Dimension(xEnd, yEnd);
		bendpoint.setRelativeDimensions(xDimension, yDimension);
		return bendpoint;
	}
	
	private ArrayList createBendPointsForSelfReference() {
		ArrayList result = new ArrayList();
		result.add(createBendpoint(-50, 0, -50, 18));
		result.add(createBendpoint(-50, -20, -50, -10));
		return result;
	}
	
	public void setBendpoints(List bendpoints) {
		this.bendpoints = bendpoints;
	}
	
	public void addBendpoint(int index, Bendpoint bendpoint) {
		getBendpoints().add(index, bendpoint);
		notifyChange(TRANSITION_BENDPOINT_ADDED);
	}
	
	public void removeBendpoint(int index) {
		getBendpoints().remove(index);
		notifyChange(TRANSITION_BENDPOINT_REMOVED);
	}
	
	public void setBendpoint(int index, Bendpoint bendpoint) {
		getBendpoints().set(index, bendpoint);
		notifyChange(TRANSITION_BENDPOINT_MOVED);
	}
	
	public String getName() {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
		return node == null ? null : node.getNodeValue();
	}

	public void setName(String name) {
		if (name == null) {
			getNode().getAttributes().removeNamedItem("name");
		} else if (canSetNameTo(name)) {
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
		Node source = getSource();
		if (source == null) return false;
		List list = source.getLeavingTransitions();
		for (int i = 0; i < list.size(); i++) {
			Transition transition = (Transition)list.get(i);
			if (name.equals(transition.getName())) {
				return false;
			}
		}
		return true;
	}
	
	public Node getSource() {
		IDOMNode node = (IDOMNode)getNode().getParentNode();
		if (node == null) return null;
		return (Node)node.getAdapterFor(GraphElement.class);
	}
	
	public Node getTarget() {
		org.w3c.dom.Node toAttr = getNode().getAttributes().getNamedItem("to");
		if (toAttr == null) return null;
		String targetName = toAttr.getNodeValue();
		if (targetName == null) return null;
		NodeList list = getNode().getOwnerDocument().getDocumentElement().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Node && targetName.equals(((Node)adapter).getName())) {
				return (Node)adapter;
			}			
		}
		return null;
	}
	
	public void setTargetName(String name) {
		org.w3c.dom.Node toAttr = getNode().getAttributes().getNamedItem("to");
		if (toAttr == null) {
			toAttr = getNode().getOwnerDocument().createAttribute("to");
			getNode().getAttributes().setNamedItem(toAttr);
		}
		toAttr.setNodeValue(name);
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
			new TextPropertyDescriptor("name", "Name"),
			new PropertyDescriptor("source", "Source"),
			new PropertyDescriptor("target", "Target")
		};
	}
	
	public Object getPropertyValue(Object id) {
		if ("source".equals(id) && getSource() != null){
			return getSource().getName();
		}
		if ("target".equals(id) && getTarget() != null) {
			return getTarget().getName();
		}
		if ("name".equals(id)) {
			return getName();
		}
		return null;
	}
	
	public void setPropertyValue(Object id, Object value) {
		if ("name".equals(id)) {
			setName((String)value);
		}
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		if (target != this) return false;
		if ("canAddActions".equals(name)) {
			return "true".equals(value);
		}
		return super.testAttribute(target, name, value);
	}

}
