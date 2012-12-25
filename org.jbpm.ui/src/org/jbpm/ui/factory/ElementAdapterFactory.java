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
package org.jbpm.ui.factory;

import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.model.ElementType;
import org.jbpm.ui.model.GraphElement;
import org.w3c.dom.NodeList;

public class ElementAdapterFactory extends AbstractAdapterFactory {

	public static final ElementAdapterFactory INSTANCE = new ElementAdapterFactory();
	
	public ElementAdapterFactory() {
		super();
		setAdapterKey(this);
		setShouldRegisterAdapter(true);
	}
	
	protected INodeAdapter createAdapter(INodeNotifier target) {
		GraphElement result = getNewAdapter((IDOMNode)target);
		if (result != null) {
			adaptChildren(result);
		}
		return result;
	}
	
	private void adaptChildren(GraphElement adapter) {
		NodeList nodes = adapter.getNode().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			GraphElement childAdapter = 
				(GraphElement)adapt((INodeNotifier)nodes.item(i));
			if (childAdapter != null) {
				adapter.add(childAdapter);
			}
		}
	}
	
	private GraphElement getNewAdapter(IDOMNode node) {
		GraphElement result = null;
		ElementType elementType = ElementType.getElementType(node.getNodeName());
		if (elementType != null) {
			result = (GraphElement)elementType.getContributor().createElement();
		}
		if (result != null) {
			result.initialize(node);
		}
		return result;
	}

}
