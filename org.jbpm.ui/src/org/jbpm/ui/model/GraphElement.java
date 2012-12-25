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

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.factory.ElementAdapterFactory;
import org.jbpm.ui.util.GraphElementWorkbenchAdapter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class GraphElement extends Element 
implements NotificationMessages, EventTypes, IPropertySource, INodeAdapter {

	public static final String padding = "   ";
	private IDOMNode node;
	
	public void initialize(IDOMNode n) {
		this.node = n;
	}
	
	public ElementType getElementType() {
		return ElementType.getElementType(getNode().getNodeName());
	}
	
	public String getNamePrefix() {
		return null;
	}
	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public String getNextActionName(String eventType) {
		return "action";
	}
	
	public ProcessDefinition getProcessDefinition() {
		IDOMNode result = (IDOMNode)getNode().getOwnerDocument().getDocumentElement();
		return (ProcessDefinition)result.getAdapterFor(GraphElement.class);
	}

	public int getLevel() {
		if (getNode().getParentNode() == getNode().getOwnerDocument()) {
			return 1;
		} else {
			GraphElement graphElemnt = 
				(GraphElement)((IDOMNode)getNode().getParentNode()).getAdapterFor(GraphElement.class);
			return graphElemnt.getLevel() + 1;
		}
	}
	
	public void addAction(String eventType, Action action) {
		Event event = getEventByType(eventType);
		if (event == null) {
			event = createEvent(eventType);
			addEvent(event);
		}
		event.addAction(action);
	}
	
	public void removeAction(String eventType, Action action) {
		Event event = getEventByType(eventType);
		if (event != null) {
			event.removeAction(action);
		}
	}
	
	public void addEvent(Event event) {
	}
	
	public void removeEvent(Event event) {
	}
	
	public void removeChild(GraphElement child) {
		IDOMNode previous = (IDOMNode)child.getNode().getPreviousSibling();
		if (previous != null && previous instanceof Text) {
			getNode().removeChild(previous);
		}
		getNode().removeChild(child.getNode());
		remove(child);		
	}
	
	public void addChildBefore(GraphElement child, IDOMNode before) {
		if (before == null) {
			getNode().appendChild(child.getNode());
		} else {
			getNode().insertBefore(child.getNode(), before);
		}
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, child.getNode());
		add(child);		
	}
	
	public Event getEventByType(String eventType) {
		NodeList list = getNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			IDOMNode n = (IDOMNode)list.item(i);
			if (isEventNodeWithType(n, eventType)) {
				return (Event)n;
			}
		}
		return null;
	}
	
	private boolean isEventNodeWithType(IDOMNode n, String eventType) {
		if (n.getAdapterFor(GraphElement.class) instanceof Event) {
			IDOMNode typeNode = (IDOMNode)n.getAttributes().getNamedItem("type");
			return (typeNode != null && eventType.equals(typeNode.getNodeValue()));
		}
		return false;
	}
	
	private Event createEvent(String eventType) {
		IDOMNode result = (IDOMNode)getNode().getOwnerDocument().createElement("event");
		IDOMNode type = (IDOMNode)getNode().getOwnerDocument().createAttribute("type");
		type.setNodeValue(eventType);
		result.getAttributes().setNamedItem(type);
		return (Event)ElementAdapterFactory.INSTANCE.adapt(result);
	}

	public boolean isAdapterForType(Object type) {
		return type == GraphElement.class ;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		IDOMNode n = (IDOMNode)notifier;
		if (changedFeature != null && ((IDOMNode)changedFeature).getNodeName().equals("class")) {
			getProcessDefinition().classNameChanged((String)oldValue, (String)newValue);
			return;
		}
		if (changedFeature != null && ((IDOMNode)changedFeature).getNodeName().equals("name")) {
			notifyChange(ELEMENT_NAME_SET);
			return;
		}
		if (changedFeature != null && ((IDOMNode)changedFeature).getNodeName().equals("to")) {
			notifyChange(TRANSITION_TARGET_CHANGED);
			return;
		}
		// TODO find a clever algorithm to change the target node
//		if (changedFeature != null && ((IDOMNode)changedFeature).getNodeName().equals("to")) {
//			INodeAdapter adapter = ElementAdapterFactory.INSTANCE.adapt((IDOMNode)n);
//			if (!(adapter instanceof Transition)) return;
//			((Transition)adapter).setNewTargetName((String)newValue);
//			return;
//		}
		if (INodeNotifier.ADD == eventType) {
			if (!(newValue instanceof IDOMNode)) return;
			GraphElement adapter = 
				(GraphElement)ElementAdapterFactory.INSTANCE.adapt((IDOMNode)newValue);
			if (adapter != null) {
				((GraphElement)n.getAdapterFor(GraphElement.class)).add(
					adapter);
			}
		}
		if (INodeNotifier.REMOVE == eventType) {
			if (!(oldValue instanceof IDOMNode)) return;
			GraphElement adapter = 
				(GraphElement)ElementAdapterFactory.INSTANCE.adapt((IDOMNode)oldValue);
			if (adapter != null) {
				((GraphElement)n.getAdapterFor(GraphElement.class)).remove(
					adapter);
			}
		}
	}
	
	public String getPaddingString(int amount) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < amount; i++) {
			result.append(padding);
		}
		return result.toString();
	}
	
	public void add(GraphElement adapter) {
		System.out.println("adding node : " + node.getSource());
	}
	
	public void remove(GraphElement adapter) {
		System.out.println("removing node : " + adapter.node.getSource());
	}
	
	public IDOMNode getNode() {
		return node;
	}

	public Object getEditableValue() {
		return this;
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {
	}

	public Object getPropertyValue(Object id) {
		if ("name".equals(id)) {
			return getName() == null ? "" : getName();
		}
		return null;
	}
	
	public Object getAdapter(Class adapter) {
		if (IWorkbenchAdapter.class.equals(adapter)) {
			return new GraphElementWorkbenchAdapter(this); 
		}
		return null;
	}
	
	

}
