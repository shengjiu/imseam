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
package org.jbpm.ui.properties;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jbpm.ui.dialog.ChooseHandlerClassDialog;

public class ChooseClassDialogCellEditor extends DialogCellEditor {
	
	private String typeName;
	private String message;
	private String title;
	
	public ChooseClassDialogCellEditor(Composite parent, String typeName, String title, String message) {
		super(parent, SWT.NONE);
		this.typeName = typeName;
		this.message = message;
		this.title = title;
	}
	
	protected Object openDialogBox(Control cellEditorWindow) {
		ChooseHandlerClassDialog dialog = 
			new ChooseHandlerClassDialog(
					cellEditorWindow.getShell(), 
					typeName,
					title,
					message);
		return dialog.openDialog();
    }	

}
