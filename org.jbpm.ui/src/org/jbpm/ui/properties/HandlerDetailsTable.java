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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jbpm.ui.PluginConstants;
import org.jbpm.ui.util.AutoResizeTableLayout;

public class HandlerDetailsTable 
extends Composite implements PluginConstants{
	
	public static final String NAME = "Name";
	public static final String VALUE = "Value";
	
	Table table;
	String configType;
	Map configurationElements;
	List configurableElements;
	
	public HandlerDetailsTable(
			Composite parent, String type, Map configurationElements, List configurableElements) {
		super(parent, SWT.NONE);
		initialize(type, configurationElements, configurableElements);
		createTable();
		createLayout();
		createColumns();
		createRows();
		createTableViewer();
	}

	private void initialize(String type, Map elementMap, List elementList) {
		this.configType = type;
		this.configurableElements = elementList;
		this.configurationElements = elementMap;
		setLayout(new GridLayout(1, false));
	}
	
	private void createTable() {
		table = new Table(this, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addListener(SWT.Selection, new Listener() {
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
				configurationElements.put(item.getText(0), item.getText(1));
			} else {
				configurationElements.remove(item.getText(0));
			}
		}
	}
	
	private void createLayout() {
		AutoResizeTableLayout layout = new AutoResizeTableLayout(table);
		layout.addColumnData(new ColumnWeightData(40));
		layout.addColumnData(new ColumnWeightData(60));
		table.setLayout(layout);
	}

	private void createColumns() {
		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText(BEAN_CONFIG_TYPE.equals(configType) ? "Property Name" : "Field Name");
		TableColumn valueColumn = new TableColumn(table, SWT.NONE);
		valueColumn.setText(BEAN_CONFIG_TYPE.equals(configType) ? "Property Value" : "Field Value");
	}

	private void createRows() {
		Iterator iterator = configurableElements.iterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, key);
			if (configurationElements.containsKey(key)) {
				String value = (String)configurationElements.get(key);
				item.setChecked(true);
				if (value != null) {
					item.setText(1, value);
				}
			}
		}
	}

	private void createTableViewer() {
		CheckboxTableViewer tableViewer = new CheckboxTableViewer(table);
		tableViewer.setColumnProperties(new String[] { NAME, VALUE });
		tableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(table), new TextCellEditor(table) });
		tableViewer.setCellModifier(new CellModifier(table));
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
					configurationElements.put(selection.getText(0), value);
				}
			}
		}
		
	}
	
}
