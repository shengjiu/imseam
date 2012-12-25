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
package org.jbpm.ui.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.factory.ElementAdapterFactory;
import org.jbpm.ui.model.Action;
import org.jbpm.ui.model.GraphElement;
import org.jbpm.ui.model.Transition;

public class GraphElementAddActionCommand extends Command {
	
	private GraphElement target;
	private String eventType;
	private Action action;
	private boolean eventExists ;
	
	public void execute() {
		if (action == null) {
			createAction();
		}
		if (!(target instanceof Transition)) {
			eventExists = target.getEventByType(eventType) != null;
		}
		target.addAction(eventType, action);
	}
	
	public void undo() {
		target.removeAction(eventType, action);
		if (!(target instanceof Transition) && !eventExists) {
			target.removeEvent(target.getEventByType(eventType));
		}
	}
	
	public void setTarget(GraphElement newTarget) {
		target = newTarget;
	}
	
	public void setEventType(String eventType)  {
		this.eventType = eventType;
	}
	
	private void createAction() {
		IDOMNode node = (IDOMNode)target.getNode().getOwnerDocument().createElement("action");
		action = (Action)ElementAdapterFactory.INSTANCE.adapt(node);
		action.setName(target.getNextActionName(eventType));
	}
	
}
