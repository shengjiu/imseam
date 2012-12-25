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

public interface EventTypes {

	  public static final String EVENTTYPE_TRANSITION = "transition";
	  public static final String EVENTTYPE_BEFORE_SIGNAL = "before-signal";
	  public static final String EVENTTYPE_AFTER_SIGNAL = "after-signal";
	  public static final String EVENTTYPE_PROCESS_START = "process-start";
	  public static final String EVENTTYPE_PROCESS_END = "process-end";
	  public static final String EVENTTYPE_NODE_ENTER = "node-enter";
	  public static final String EVENTTYPE_NODE_LEAVE = "node-leave";
	  public static final String EVENTTYPE_SUPERSTATE_ENTER = "superstate-enter";
	  public static final String EVENTTYPE_SUPERSTATE_LEAVE = "superstate-leave";
	  public static final String EVENTTYPE_SUBPROCESS_CREATED = "subprocess-created";
	  public static final String EVENTTYPE_SUBPROCESS_ENDED = "subprocess-ended";
	  
}
