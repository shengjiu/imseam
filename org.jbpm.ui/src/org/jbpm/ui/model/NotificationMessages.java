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


public interface NotificationMessages {

	public static final int ELEMENT_NAME_SET = 1;
	public static final int ELEMENT_EVENT_ADDED = 2;
	public static final int ELEMENT_EVENT_REMOVED = 3;
	public static final int ELEMENT_ACTION_ADDED = 4;
	public static final int ELEMENT_ACTION_REMOVED = 5;
	public static final int ELEMENT_TASK_ADDED = 6;
	public static final int ELEMENT_TASK_REMOVED = 7;
	public static final int ELEMENT_ASSIGNMENT_ADDED = 8;
	public static final int ELEMENT_ASSIGNMENT_REMOVED = 9;
	public static final int ELEMENT_CLASS_CHANGED = 10;
	
	public static final int PROCESS_DEFINITION_NODE_ADDED = 12;
	public static final int PROCESS_DEFINITION_NODE_REMOVED = 13;
	public static final int PROCESS_DEFINITION_SWIMLANE_ADDED = 14;
	public static final int PROCESS_DEFINITION_SWIMLANE_REMOVED = 15;
	
	public static final int NODE_X_SET = 21;
	public static final int NODE_Y_SET = 22;
	public static final int NODE_CONSTRAINT_SET = 27;
	public static final int NODE_LEAVING_TRANSITION_ADDED = 23;
	public static final int NODE_LEAVING_TRANSITION_REMOVED = 24;
	public static final int NODE_ARRIVING_TRANSITION_ADDED = 25;
	public static final int NODE_ARRIVING_TRANSITION_REMOVED = 26;
	
	public static final int TRANSITION_BENDPOINT_ADDED = 31;
	public static final int TRANSITION_BENDPOINT_REMOVED = 32;
	public static final int TRANSITION_BENDPOINT_MOVED = 33;
	public static final int TRANSITION_TARGET_CHANGED = 34;
		
}
