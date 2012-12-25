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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Event extends GraphElement {

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
	
	public String getType() {
		IDOMNode type = (IDOMNode)getNode().getAttributes().getNamedItem("type");
		return type == null ? null : type.getNodeValue();
	}
	
	public String getName() {
		String result = getType();
		return result == null ? "" : result;
	}
	
	public void setName(String name) {
		// changing the name of an event makes no sense.
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
	
	public Action getActionByName(String name) {
		List actions = getActions();
		for (int i = 0; i < actions.size(); i++) {
			if (name.equals(((Action)actions.get(i)).getName())) {
				return (Action)actions.get(i);
			}
		}
		return null;
	}
	
	public void addAction(Action action) {
		IDOMNode last = (IDOMNode)getNode().getLastChild();
		if (last == null) {
			last = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel() - 1));
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
		IDOMNode text = (IDOMNode)getNode().getOwnerDocument().createTextNode("\n" + getPaddingString(getLevel()));
		getNode().insertBefore(text, action.getNode());
		add(action);
	}
	
	public void add(GraphElement adapter) {
		if (adapter instanceof Action) {
			notifyChange(ELEMENT_ACTION_ADDED);
		}
	}
	
	public void removeAction(Action action) {
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
	
	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (changedFeature != null && ((IDOMNode)changedFeature).getNodeName().equals("type")) {
			notifyChange(ELEMENT_NAME_SET);
		}
		super.notifyChanged(notifier, eventType, changedFeature, oldValue, newValue, pos);
	}
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[0];
	}

	public Object getPropertyValue(Object id) {
		return null;
	}

	public void setPropertyValue(Object id, Object value) {
	}
	
	public boolean testAttribute(Object target, String name, String value) {
		if (target != this) return false;
		if ("canAddActions".equals(name)) {
			return "true".equals(value);
		}
		return super.testAttribute(target, name, value);
	}

	
}
