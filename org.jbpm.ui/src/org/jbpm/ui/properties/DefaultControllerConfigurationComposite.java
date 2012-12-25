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

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jbpm.ui.SharedImages;
import org.jbpm.ui.model.Variable;
import org.jbpm.ui.util.AutoResizeTableLayout;


public class DefaultControllerConfigurationComposite extends Composite {
	
	private static final String NAME_PROPERTY = "name";
	private static final String READ_PROPERTY = "read";
	private static final String WRITE_PROPERTY = "write";
	private static final String REQUIRED_PROPERTY = "required";
	private static final String MAP_PROPERTY = "map";
	
	private static final ImageDescriptor checked = ImageDescriptor.createFromFile(
			SharedImages.class, "icon/checked.gif");
	private static final ImageDescriptor unchecked = ImageDescriptor.createFromFile(
			SharedImages.class, "icon/unchecked.gif");

	TableViewer tableViewer;
	
	public DefaultControllerConfigurationComposite(Composite parent, List controllerVariables) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		createLabel();
		createTableViewer(controllerVariables);
		createAddButton();
		createRemoveButton();
	}
	
	private void createLabel() {
		Label label = new Label(this, SWT.NONE);
		label.setText("Define the used variables :");
		GridData labelData = new GridData(GridData.FILL_HORIZONTAL);
		labelData.horizontalSpan = 2;
		label.setLayoutData(labelData);
	}

	private void createTableViewer(List controllerVariables) {
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		initializeTable();
		initializeContentProvider();
		initializeLabelProvider();
		initializeCellEditors();
		initializeContents(controllerVariables);
		GridData tableData = new GridData(GridData.FILL_BOTH);
		tableData.verticalSpan = 2;
		tableData.widthHint = 350;
		tableViewer.getControl().setLayoutData(tableData);
	}
	
	private void initializeContents(List controllerVariables) {
		tableViewer.setInput(controllerVariables);
	}
	
	private void initializeLabelProvider() {
		tableViewer.setLabelProvider(new ITableLabelProvider() {
			public Image getColumnImage(Object element, int columnIndex) {
				Variable item = (Variable)element;
				Boolean check = null;
				switch (columnIndex) {
					case 1: check = item.read; break;
					case 2: check = item.write; break;
					case 3: check = item.required;
				}
				if (check == null) {
					return null;
				}
				if (check.booleanValue()) {
					return SharedImages.INSTANCE.getImage(checked);					
				} else {
					return SharedImages.INSTANCE.getImage(unchecked);
				}
				
			}
			public String getColumnText(Object element, int columnIndex) {
				Variable item = (Variable)element;
				switch(columnIndex) {
					case 0: 
						return item.name;
					case 4: 
						return item.mappedName;
					default:
						return null;
				}
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			public void removeListener(ILabelProviderListener listener) {
			}
		});
	}
	
	private void initializeContentProvider() {
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((List)inputElement).toArray();
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
	}
	
	private void initializeCellEditors() {
		Table table = tableViewer.getTable();
		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return true;
			}
			public Object getValue(Object element, String property) {
				Object result = "";
				Variable item = (Variable)element;
				if (NAME_PROPERTY.equals(property)) {
					result = item.name == null ? "" : item.name;
				} else if (READ_PROPERTY.equals(property)) {
					result = item.read;
				} else if (WRITE_PROPERTY.equals(property)) {
					result = item.write;
				} else if (REQUIRED_PROPERTY.equals(property)) {
					result = item.required;
				} else if (MAP_PROPERTY.equals(property)) {
					result = item.mappedName == null ? "" : item.mappedName;
				}
				return result;
			}
			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem)element;
				Variable item = (Variable)tableItem.getData();
				if (NAME_PROPERTY.equals(property)) {
					item.name = (String)value;
				} else if (READ_PROPERTY.equals(property)) {
					item.read = (Boolean)value;
				} else if (WRITE_PROPERTY.equals(property)) {
					if (!item.required.booleanValue()) {
						item.write = (Boolean)value;
					}
				} else if (REQUIRED_PROPERTY.equals(property)) {
					boolean required = ((Boolean)value).booleanValue();
					if (required) {
						item.write = Boolean.TRUE;
					}
					item.required = (Boolean)value;
				} else if (MAP_PROPERTY.equals(property)) {
					item.mappedName = (String)value;
				}
				tableViewer.refresh(item);
			}
		});
		tableViewer.setCellEditors(
				new CellEditor[] { 
						new TextCellEditor(table), 
						new CheckboxCellEditor(table),
						new CheckboxCellEditor(table),
						new CheckboxCellEditor(table),
						new TextCellEditor(table)});		
		tableViewer.setColumnProperties(new String[] {
				NAME_PROPERTY, READ_PROPERTY, WRITE_PROPERTY, REQUIRED_PROPERTY, MAP_PROPERTY				
			});
	}
	
	private void initializeTableColumns() {
		Table table = tableViewer.getTable();
		TableLayout layout = (TableLayout)table.getLayout();
		TableColumn nameColumn = new TableColumn(table, SWT.CENTER);
		nameColumn.setText("Name");
		ColumnWeightData nameColumnData = new ColumnWeightData(20, 100);
		layout.addColumnData(nameColumnData);
		TableColumn readColumn = new TableColumn(table, SWT.CENTER);
		readColumn.setText("Read");
		ColumnWeightData readColumnData = new ColumnWeightData(10, 50);
		layout.addColumnData(readColumnData);
		TableColumn writeColumn = new TableColumn(table, SWT.CENTER);
		writeColumn.setText("Write");
		ColumnWeightData writeColumnData = new ColumnWeightData(10, 50);
		layout.addColumnData(writeColumnData);
		TableColumn requiredColumn = new TableColumn(table, SWT.CENTER);
		requiredColumn.setText("Required");
		ColumnWeightData requiredColumnData = new ColumnWeightData(15, 75);
		layout.addColumnData(requiredColumnData);
		TableColumn mappedNameColumn = new TableColumn(table, SWT.CENTER);
		mappedNameColumn.setText("Mapped Name");
		ColumnWeightData mappedNameColumnData = new ColumnWeightData(30, 150);
		layout.addColumnData(mappedNameColumnData);
	}
	
	private void initializeTable() {
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayout(new AutoResizeTableLayout(table));
		initializeTableColumns();
	}
	
	private void createAddButton() {
		Button addButton = new Button(this, SWT.PUSH);
		addButton.setText("Add");
		GridData buttonData = new GridData();
		buttonData.widthHint = 60;
		addButton.setLayoutData(buttonData);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Variable item = new Variable("<name>", true, false, false, "");
				((List)tableViewer.getInput()).add(item);
				tableViewer.refresh();
			}		
		});
	}

	private void createRemoveButton() {
		Button removeButton = new Button(this, SWT.PUSH);
		removeButton.setText("Remove");
		GridData buttonData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonData.widthHint = 60;
		removeButton.setLayoutData(buttonData);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = ((IStructuredSelection)tableViewer.getSelection());
				if (selection == null) return;
				Variable variable = (Variable)selection.getFirstElement();
				((List)tableViewer.getInput()).remove(variable);
				tableViewer.refresh();
			}
		});
	}
	
	public List getVariables() {
		return (List)tableViewer.getInput();
	}
	
}
