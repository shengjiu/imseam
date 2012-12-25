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
package org.jbpm.ui.editor.form.swimlane;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.model.Swimlane;
import org.jbpm.ui.util.AutoResizeTableLayout;

public class SwimlaneAssignmentHandlerConfigurationTable implements PluginConstants {

	public static final String NAME = "Name";
	public static final String VALUE = "Value";
	
	private Table configurationTable;
	private FormToolkit toolkit;
	private Composite parent;	
	private Swimlane swimlane;
	
	private List rows;
	
	public SwimlaneAssignmentHandlerConfigurationTable(
			FormToolkit toolkit, Composite parent, Swimlane swimlane, List rows) {
		this.toolkit = toolkit;
		this.parent = parent;
		this.swimlane = swimlane;
		this.rows = rows;
	}
	
	public void create() {
		if (rows == null || rows.isEmpty()) return;
		createConfigurationTable();
		createConfigurationTableColumns();
		createConfigurationTableRows();		
		createConfigurationTableViewer();
	}
	
	private void createConfigurationTable() {
		configurationTable = toolkit.createTable(parent, SWT.CHECK | SWT.FULL_SELECTION);
		configurationTable.setLinesVisible(true);
		configurationTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		AutoResizeTableLayout layout = new AutoResizeTableLayout(configurationTable);
		layout.addColumnData(new ColumnWeightData(40));
		layout.addColumnData(new ColumnWeightData(60));
		configurationTable.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		configurationTable.setLayoutData(gridData);
		configurationTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleTableItemSelection(event);
			}					
		});
	}
	
	private void handleTableItemSelection(Event event) {
		if (event.detail != SWT.CHECK) return;
		if (event.item instanceof TableItem) {
			TableItem item = (TableItem)event.item;
			if (item.getChecked()) {
				String value = item.getText(1);
				value = value == null ? "" : value;
				swimlane.addAssignmentConfigurationInfo(item.getText(0), value );
			} else {
				swimlane.removeAssignmentConfigurationInfo(item.getText(0));
			}
		}
	}

	
	private void createConfigurationTableRows() {
		for (int i = 0; i < rows.size(); i++) {
			String value = swimlane.getAssignmentConfigurationInfo((String)rows.get(i));
			TableItem item = new TableItem(configurationTable, SWT.NONE);
			item.setText(0, (String)rows.get(i));
			if (value != null) {
				item.setChecked(true);
				item.setText(1, value);
			}
		}
	}

	private void createConfigurationTableColumns() {
		TableColumn nameColumn = new TableColumn(configurationTable, SWT.NONE);
		nameColumn.setText("Field Name");
		TableColumn valueColumn = new TableColumn(configurationTable, SWT.NONE);
		valueColumn.setText("Field Value");
	}
	
	private void createConfigurationTableViewer() {
		CheckboxTableViewer tableViewer = new CheckboxTableViewer(configurationTable);
		tableViewer.setColumnProperties(new String[] { NAME, VALUE });
		tableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(configurationTable), new TextCellEditor(configurationTable) });
		tableViewer.setCellModifier(new CellModifier(configurationTable));
	}
	
	public void dispose() {
		configurationTable.dispose();
	}

	private class CellModifier implements ICellModifier {
		
		Table t;
		
		public CellModifier(Table table) {
			this.t = table;
		}
		
		public boolean canModify(Object element, String property) {
			return VALUE.equals(property);
		}

		public Object getValue(Object element, String property) {
			TableItem selection = t.getSelection()[0];
			if (NAME.equals(property)) {
				return selection.getText(0);
			} else {
				return selection.getText(1);
			}
		}

		public void modify(Object element, String property, Object value) {
			TableItem selection = t.getSelection()[0];
			if (NAME.equals(property)) {
				selection.setText(0, (String)value);
			} else {
				selection.setText(1, (String)value);
				if (selection.getChecked()) {
					swimlane.setAssignmentConfigurationInfo(selection.getText(0), selection.getText(1));
				}
			}
		}
		
	}
	
}
