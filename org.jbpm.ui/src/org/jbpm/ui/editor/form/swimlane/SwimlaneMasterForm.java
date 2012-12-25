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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jbpm.ui.factory.ElementAdapterFactory;
import org.jbpm.ui.model.NotificationMessages;
import org.jbpm.ui.model.ProcessDefinition;
import org.jbpm.ui.model.Swimlane;

public class SwimlaneMasterForm implements Observer, NotificationMessages {

	private ProcessDefinition processDefinition;
	private FormToolkit toolkit;
	private Composite composite;
	
	Button addButton;
	TableViewer swimlaneTable;
	
	public SwimlaneMasterForm(FormToolkit toolkit, Composite composite) {
		this.toolkit = toolkit;
		this.composite = composite;
	}	
	
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		if (this.processDefinition != null) {
			this.processDefinition.deleteObserver(this);
		}
		this.processDefinition = processDefinition;
		if (this.processDefinition != null) {
			this.processDefinition.addObserver(this);
		}
	}
	
	public void create() {
		Section master = toolkit.createSection(composite, Section.TITLE_BAR);		
		master.marginHeight = 5;
		master.marginWidth = 5;
		master.setText("All Swimlanes");
		
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		master.setLayoutData(layoutData);
		
		Composite masterClient = toolkit.createComposite(master);
		master.setClient(masterClient);
		toolkit.paintBordersFor(masterClient);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.numColumns = 2;
		masterClient.setLayout(layout);
		
		Table table = toolkit.createTable(masterClient, SWT.NORMAL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		swimlaneTable = new TableViewer(table);
		swimlaneTable.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {	
				return processDefinition.getSwimlanes().toArray();
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}			
		});
		swimlaneTable.setLabelProvider(new LabelProvider() {
			public String getText(Object item) {
				if (item instanceof Swimlane) {
					return ((Swimlane)item).getName();
				}
				return super.getText(item);
			}			
		});
		swimlaneTable.setInput(processDefinition.getSwimlanes());
		addButton = toolkit.createButton(masterClient, "Add", SWT.PUSH);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.TOP;
		addButton.setLayoutData(gridData);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addNewSwimlane();
			}
		});		
	}
	
	private void addNewSwimlane() {
		IDOMNode node = (IDOMNode)processDefinition.getNode().getOwnerDocument().createElement("swimlane");
		Swimlane swimlane = (Swimlane)ElementAdapterFactory.INSTANCE.adapt(node);
		swimlane.setName(processDefinition.getNextSwimlaneName());
		processDefinition.addSwimlane(swimlane);
		swimlaneTable.setSelection(new StructuredSelection(swimlane), true);				
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		swimlaneTable.addSelectionChangedListener(listener);
	}

	public void update(Observable arg0, Object arg1) {
		int messageId = ((Integer)arg1).intValue();
		switch(messageId) {
			case PROCESS_DEFINITION_SWIMLANE_ADDED:
			case PROCESS_DEFINITION_SWIMLANE_REMOVED:
			case ELEMENT_NAME_SET:
				refreshSwimlaneTable();
				break;
			default:
				break;
		}		
	}
	
	private Swimlane getSelectedSwimlane() {
		IStructuredSelection selection = (IStructuredSelection)swimlaneTable.getSelection();
		if (selection == null) return null;
		return  (Swimlane)selection.getFirstElement();
	}
	
	private void refreshSwimlaneTable() {
		Swimlane selection = getSelectedSwimlane();
		swimlaneTable.setInput(processDefinition.getSwimlanes());
		if (processDefinition.getSwimlanes().contains(selection)) {
			swimlaneTable.setSelection(new StructuredSelection(selection), true);							
		}
	}
}
