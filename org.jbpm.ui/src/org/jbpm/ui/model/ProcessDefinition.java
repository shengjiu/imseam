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
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ProcessDefinition extends GraphElement  {
	
	private class ClassNameChangedRouter extends Observable {
		void classNameChanged(String oldClassName, String newClassName) {
			setChanged();
			notifyObservers(new String[] {oldClassName, newClassName});
		}
	}
	
	private Dimension dimension;
	private final ClassNameChangedRouter router = new ClassNameChangedRouter();
	
	public void addObserverOfClassNameChanges(Observer o) {
		router.addObserver(o);
	}
	
	public void removeObserverOfClassNameChanges(Observer o) {
		router.deleteObserver(o);
	}
	
	public void classNameChanged(String from, String to) {
		router.classNameChanged(from, to);
	}
	
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	public Dimension getDimension() {
		if (dimension == null) {
			dimension = new Dimension(0, 0);
		}
		return dimension;
	}

	public void initialize(IDOMNode node) {
		super.initialize(node);
		initializeName();
	}
	
	protected void initializeName() {
		if (getName() == null) {
			setName("process");
		}
	}
	
	public String getNextNodeName(Node node)  {
		int runner = 1;
		while (true) {
			String candidate = node.getNamePrefix() + runner;
			if (getNodeByName(candidate) == null) {
				return candidate;
			}
			runner ++;
		}
	}
	
	public String getNextSwimlaneName()  {
		int runner = 1;
		while (true) {
			String candidate = "swimlane" + runner;
			if (getSwimlaneByName(candidate) == null) {
				return candidate;
			}
			runner ++;
		}
	}
	
	public Swimlane getSwimlaneByName(String name) {
		List swimlanes = getSwimlanes();
		for (int i = 0; i < swimlanes.size(); i++) {
			if (name.equals(((Swimlane)swimlanes.get(i)).getName())) {
				return (Swimlane)swimlanes.get(i);
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
	
	public List getSwimlanes() {
		ArrayList result = new ArrayList();
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Swimlane) {
				result.add(adapter);
			}
		}
		return result;
	}
	
	public String getName() {
		IDOMNode node = (IDOMNode)getNode().getAttributes().getNamedItem("name");
		return node == null ? null : node.getNodeValue();
	}
	
	public void setName(String name) {
		if (name == null) {
			getNode().getAttributes().removeNamedItem("name");
		} else {
			org.w3c.dom.Node node = getNode().getAttributes().getNamedItem("name");
			if (node == null) {
				node = getNode().getOwnerDocument().createAttribute("name");
				getNode().getAttributes().setNamedItem(node);					
			}
			node.setNodeValue(name);
		}
		notifyChange(ELEMENT_NAME_SET);
	}
	
	public boolean containsStartState() {
		List nodes = getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof StartState) {
				return true;
			}
		}
		return false;
	}
	
	public void addSwimlane(Swimlane swimlane) {
		GraphElement first = getFirstEventOrNode();
		IDOMNode before = null;
		if (first == null) {
			Swimlane last = getLastSwimlane();
			if (last == null) {
				before = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
				getNode().appendChild(before);
			} else {
				before = (IDOMNode)last.getNode().getNextSibling();	
			}			
		} else {
			before = (IDOMNode)first.getNode().getPreviousSibling();
		}
		addSwimlaneBefore(swimlane, before);
	}
	
	public void addSwimlaneBefore(Swimlane swimlane, IDOMNode before) {
		getNode().insertBefore(swimlane.getNode(), before);
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, swimlane.getNode());
		add(swimlane);
	}
	
	private GraphElement getFirstEventOrNode() {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter candidate = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Event || candidate instanceof Node) {
				return (GraphElement)candidate;
			}
		}
		return null;
	}
	
	private Swimlane getLastSwimlane() {
		NodeList list = getNode().getChildNodes();
		for (int i = list.getLength(); i > 0; i--) {
			INodeAdapter candidate = ((IDOMNode)list.item(i - 1)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Swimlane) {
				return (Swimlane)candidate;
			}
		}
		return null;
	}
	
		public void addEvent(Event event) {
		Node first = getFirstNode();
		IDOMNode before = null;
		if (first == null) {
			Event last = getLastEvent();
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
	
	public void removeEvent(Event event) {
		IDOMNode previous = (IDOMNode)event.getNode().getPreviousSibling();
		if (previous != null && previous instanceof Text) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(event.getNode());
		remove(event);
	}
	
	private Node getFirstNode() {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			INodeAdapter candidate = ((IDOMNode)list.item(i)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Node) {
				return (Node)candidate;
			}
		}
		return null;
	}
	
	private Event getLastEvent() {
		NodeList list = getNode().getChildNodes();
		for (int i = list.getLength(); i > 0; i--) {
			INodeAdapter candidate = ((IDOMNode)list.item(i - 1)).getAdapterFor(GraphElement.class);
			if (candidate instanceof Event) {
				return (Event)candidate;
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
	
	public void addNode(Node node) {
		IDOMNode last = (IDOMNode)getNode().getLastChild();
		if (last == null) {
			last = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
			getNode().appendChild(last);
		}
		addNodeBefore(node, last);
	}
	
	public void addNodeBefore(Node node, IDOMNode before) {
		if (before == null) {
			getNode().appendChild(node.getNode());
		} else {
			getNode().insertBefore(node.getNode(), before);
		}
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, node.getNode());
		add(node);
	}
	
	public void removeNode(Node node) {
		IDOMNode previous = (IDOMNode)node.getNode().getPreviousSibling();
		if (previous != null && previous instanceof Text) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(node.getNode());
		remove(node);
	}
	
	public void add(GraphElement adapter) {
		if (adapter instanceof Node) {
			notifyChange(PROCESS_DEFINITION_NODE_ADDED);
		} else if (adapter instanceof Event) {
			notifyChange(ELEMENT_EVENT_ADDED);
		} else if (adapter instanceof Swimlane) {
			notifyChange(PROCESS_DEFINITION_SWIMLANE_ADDED);
		}
	}
	
	public void remove(GraphElement adapter) {
		if (adapter instanceof Node) {
			notifyChange(PROCESS_DEFINITION_NODE_REMOVED);
		} else if (adapter instanceof Event) {
			notifyChange(ELEMENT_EVENT_REMOVED);
		} else if (adapter instanceof Swimlane) {
			notifyChange(PROCESS_DEFINITION_SWIMLANE_REMOVED);
		}
	}
	
	public Node getNodeByName(String name) {
		List nodes = getNodes();
		for (int i = 0; i < nodes.size(); i++) {
			if (name.equals(((Node)nodes.get(i)).getName())) {
				return (Node)nodes.get(i);
			}
		}
		return null;
	}
	
	public List getNodes() {
		ArrayList result = new ArrayList();
		NodeList nodeList = getNode().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			INodeAdapter adapter = ((IDOMNode)nodeList.item(i)).getAdapterFor(GraphElement.class);
			if (adapter instanceof Node) {
				result.add(adapter);
			}
		}
		return result;
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

	public String[] getSupportedEventTypes() {
		return new String[]{
				Event.EVENTTYPE_PROCESS_START,
				Event.EVENTTYPE_PROCESS_END,
				Event.EVENTTYPE_BEFORE_SIGNAL,
				Event.EVENTTYPE_AFTER_SIGNAL,
				Event.EVENTTYPE_NODE_ENTER,
				Event.EVENTTYPE_NODE_LEAVE
		};
	}

	public boolean testAttribute(Object target, String name, String value) {
		if (target != this) return false;
		if ("canAddNodeEvents".equals(name)) {
			return "true".equals(value);
		}
		if ("canAddProcessEvents".equals(name)) {
			return "true".equals(value);
		}
		return super.testAttribute(target, name, value);
	}
	
}
