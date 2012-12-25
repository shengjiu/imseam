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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.factory.ElementAdapterFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Node extends GraphElement {

	private Rectangle constraint;
	
	public Dimension getInitialSize() {
		return new Dimension(140, 40);
	}
	
	public String getNamePrefix() {
		return "node";
	}
	
	public Rectangle getConstraint() {
		if (constraint == null) {
			constraint = new Rectangle(new Point(0,0), getInitialSize());
		}
		return constraint;
	}
	
	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
		notifyChange(NODE_CONSTRAINT_SET);
	}
	
	public String getNextTransitionName() {
		int runner = 1;
		while (true) {
			String candidate = "";
            if (runner>1) candidate = "tr" + runner;
			if (getTransitionByName(candidate) == null) {
				return candidate;
			}
			runner ++;
		}		
	}
	
	public Transition getTransitionByName(String name) {
		List transitions = getLeavingTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			if (name.equals(((Transition)transitions.get(i)).getName())) {
				return (Transition)transitions.get(i);
			}
		}
		return null;
	}
	
	public String getNextActionName(String eventType) {
		String result = "action1";
		Event event = getEventByType(eventType);
		if (event != null) {
			return event.getNextActionName();
		}
		return result;
	}
	
	public Event getEventByType(String eventType) {
		List events = getEvents();
		for (int i = 0; i < events.size(); i++) {
			if (eventType.equals(((Event)events.get(i)).getType())) {
				return (Event)events.get(i);
			}
		}
		return null;
	}
	
	public List getEvents() {
		ArrayList result = new ArrayList();
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Event) {
				result.add(adapter);
			}
		}
		return result;
	}
	
	public boolean isPossibleChildOf(ProcessDefinition candidateParent) {
		return (getProcessDefinition().equals(candidateParent));
	}
	
	public void addEvent(Event event) {
		GraphElement first = getEventBeforeInsertionPoint();
		IDOMNode before = null;
		if (first == null) {
			GraphElement last = getEventAfterInsertionPoint();
			if (last == null) {
				before = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
				getNode().appendChild(before);
			} else {
				before = (IDOMNode)last.getNode().getNextSibling();				
			}
		} else {
			before = (IDOMNode)first.getNode().getPreviousSibling();
		}
		addEventBefore(event, before);
	}
	
	protected GraphElement getEventAfterInsertionPoint() {
		NodeList list = getNode().getChildNodes();
		for (int i = list.getLength(); i > 0; i--) {
			INodeAdapter candidate = ((IDOMNode)list.item(i - 1)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Event) {
				return (GraphElement)candidate;
			}
		}
		return null;
	}
	
	public void removeEvent(Event event) {
		IDOMNode previous = (IDOMNode)event.getNode().getPreviousSibling();
		if (previous != null && previous instanceof Text) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(event.getNode());
		remove(event);
	}
	
	protected GraphElement getEventBeforeInsertionPoint() {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter candidate = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Transition) {
				return (GraphElement)candidate;
			}
		}
		return null;
	}
		
	public void addEventBefore(Event event, IDOMNode before) {
		getNode().insertBefore(event.getNode(), before);
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, event.getNode());
		add(event);
	}
	
	public void addLeavingTransition(Transition transition) {
		IDOMNode last = (IDOMNode)getNode().getLastChild();
		if (last == null) {
			last = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n   ");
			getNode().appendChild(last);
		}
		addTransitionBefore(transition, last);
	}
	
	public void addTransitionBefore(Transition transition, IDOMNode before) {
		if (before == null) {
			getNode().appendChild(transition.getNode());
		} else {
			getNode().insertBefore(transition.getNode(), before);
		}
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n      ");
		getNode().insertBefore(text, transition.getNode());
		add(transition);
	}
	
	public void add(GraphElement adapter) {
		if (adapter instanceof Transition) {
			notifyChange(NODE_LEAVING_TRANSITION_ADDED);
			Node target = ((Transition)adapter).getTarget();
			if (target != null) {
				target.notifyChange(NODE_ARRIVING_TRANSITION_ADDED);
			}
		} else if (adapter instanceof Event) {
			notifyChange(ELEMENT_EVENT_ADDED);
		}
	}
	
	public void removeLeavingTransition(Transition transition) {
		IDOMNode previous = (IDOMNode)transition.getNode().getPreviousSibling();
		if (previous != null && previous instanceof Text) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(transition.getNode());
		remove(transition);
	}

	public void remove(GraphElement adapter) {
		if (adapter instanceof Transition) {
			notifyChange(NODE_LEAVING_TRANSITION_REMOVED);
			Node target = ((Transition)adapter).getTarget();
			if (target != null) {
				target.notifyChange(NODE_ARRIVING_TRANSITION_REMOVED);
			}
		} else if (adapter instanceof Event) {
			notifyChange(ELEMENT_EVENT_REMOVED);
		}
	}
	
	public List getLeavingTransitions() {
		ArrayList result = new ArrayList();
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Transition) {
				result.add(adapter);
			}
		}
		return result;
	}
	
	public List getArrivingTransitions() {
		ArrayList result = new ArrayList();
		NodeList list = getNode().getOwnerDocument().getElementsByTagName("transition");
		for (int i = 0; i < list.getLength(); i++) {
			org.w3c.dom.Node to = ((IDOMNode)list.item(i)).getAttributes().getNamedItem("to");
			if (to != null && to.getNodeValue() != null && to.getNodeValue().equals(getName())) {
				result.add(((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class));
			}
		}
		return result;
	}
	
	public String getName() {
		org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
		return node == null ? null : node.getNodeValue();
	}
	
	public void setName(String name) {
		if (name != null && !name.equals(getName()) && canSetNameTo(name)) {
			List transitions = getArrivingTransitions();
			uncheckedSetName(name);
			updateTransitions(transitions);
		}		
		notifyChange(ELEMENT_NAME_SET);
	}
	
	private void updateTransitions(List transitions) {
		for (int i = 0; i < transitions.size(); i++) {
			Transition transition = (Transition)transitions.get(i);
			transition.setTargetName(getName());
		}
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
		ProcessDefinition processDefinition = getProcessDefinition();
		if (processDefinition == null) return false;
		Node node = processDefinition.getNodeByName(name);
		return node == null || node == this; 
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
			new TextPropertyDescriptor("name", "Name")		};
	}

	public Object getPropertyValue(Object id) {
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
	
	//************************************************************************************
	// Action stuff, this should all be refactored...
	//************************************************************************************
	
	public boolean hasAction() {
		return getAction() != null;
	}
	
	public Action getAction() {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Action) {
				return (Action)adapter;
			}
		}
		return null;
	}
	
	public void addAction() {
		if (hasAction()) return;
		GraphElement first = getActionBeforeInsertionPoint();
		IDOMNode before = null;
		if (first == null) {
			before = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
			getNode().appendChild(before);
		} else {
			before = (IDOMNode)first.getNode().getPreviousSibling();
		}
		addActionBefore(before);
	}
	
	private void addActionBefore(IDOMNode before) {
		IDOMNode node = (IDOMNode)getNode().getOwnerDocument().createElement("action");
		Action action = (Action)ElementAdapterFactory.INSTANCE.adapt(node);
		getNode().insertBefore(action.getNode(), before);
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, action.getNode());
		add(action);
	}
	
	private GraphElement getActionBeforeInsertionPoint() {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter candidate = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Event) {
				return (GraphElement)candidate;
			}
		}
		return null;
	}
		
	public void removeAction() {
		if (!hasAction()) return;
		IDOMNode node = getAction().getNode();
		if (node == null) return;
		IDOMNode previous = (IDOMNode)node.getPreviousSibling();
		if (previous != null) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(node);
	}
	
	//*************************************************************************************
	// End Action stuff
	//****************************************************************************************
	
	public String[] getSupportedEventTypes() {
		return new String[]{
				Event.EVENTTYPE_NODE_ENTER,
				Event.EVENTTYPE_NODE_LEAVE,
				Event.EVENTTYPE_BEFORE_SIGNAL,
				Event.EVENTTYPE_AFTER_SIGNAL
		};
		
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		if (target != this) return false;
		if ("canAddNodeEvents".equals(name)) {
			return "true".equals(value);
		}
		return super.testAttribute(target, name, value);
	}
}
