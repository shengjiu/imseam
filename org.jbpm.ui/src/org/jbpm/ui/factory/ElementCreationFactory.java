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

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.editor.DesignerEditor;
import org.w3c.dom.Document;

public class ElementCreationFactory implements CreationFactory {

	private DesignerEditor designerEditor;
	private String elementType;
	
	public ElementCreationFactory(String elementType, DesignerEditor editor) {
		this.designerEditor = editor;
		this.elementType = elementType;
	}

	public Object getNewObject() {
		return ElementAdapterFactory.INSTANCE.adapt(
				(IDOMNode)getDocument().createElement(elementType));
	}

	public Object getObjectType() {
		return elementType;
	}
	
	protected Document getDocument() {
		return designerEditor.getProcessDefinition().getNode().getOwnerDocument();
	}
	
}
