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
package org.jbpm.ui.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProcessDeploymentWizardPage extends WizardPage {
	
	Text serverNameText; 
	Text serverPortText;
	Text deployerText;
	Text locationText;
	Button saveLocallyCheckbox;
	Button searchButton;
	
	public ProcessDeploymentWizardPage() {
		super("Process Definition Deployment");
		setTitle("Deploy Process Definition");
		setDescription("Deploy a process definition");	
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = createClientArea(parent);		
		createRemoteDeploymentGroup(composite);
		createLocalSaveGroup(composite);
		setControl(composite);
		Dialog.applyDialogFont(composite);		
		setPageComplete(true);
	}

	private Composite createClientArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		layout.numColumns= 1;
		composite.setLayout(layout);
		return composite;
	}
	
	private void createRemoteDeploymentGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Http Deployment Settings");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		createServerNameField(group);
		createServerPortField(group);
		createDeployerField(group);
	}
	
	private void createServerNameField(Composite parent) {
		Label serverNameLabel = new Label(parent, SWT.NONE);
		serverNameLabel.setText("Server Name: ");
		serverNameText = new Text(parent, SWT.BORDER);
		serverNameText.setText("localhost");
		serverNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	private void createServerPortField(Composite parent) {
		Label serverPortLabel = new Label(parent, SWT.NONE);
		serverPortLabel.setText("Server Port: ");
		serverPortText = new Text(parent, SWT.BORDER);
		serverPortText.setText("8080");
		serverPortText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	private void createDeployerField(Composite parent) {
		Label deployerLabel = new Label(parent, SWT.NONE);
		deployerLabel.setText("Deployer: ");
		deployerText = new Text(parent, SWT.BORDER);
		deployerText.setText("jbpm/upload");
		deployerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	private void createLocalSaveGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("Local Save Settings");		
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);
		createSaveLocallyCheckbox(group);
		createLocationField(group);
	}
	
	private void createSaveLocallyCheckbox(Composite parent) {
		saveLocallyCheckbox = new Button(parent, SWT.CHECK);
		saveLocallyCheckbox.setText("Save Process Archive Locally");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		saveLocallyCheckbox.setLayoutData(gridData);
		saveLocallyCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleSaveLocallyCheckboxSelected();
			}
		});
	}
	
	private void handleSaveLocallyCheckboxSelected() {
		locationText.setEnabled(saveLocallyCheckbox.getSelection());
		searchButton.setEnabled(saveLocallyCheckbox.getSelection());
	}
	
	private void createLocationField(Composite parent) {
		Label locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setText("Location: ");
		locationText = new Text(parent, SWT.BORDER);
		locationText.setEnabled(false);
		locationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchButton = new Button(parent, SWT.PUSH);
		searchButton.setText("Search...");
		searchButton.setEnabled(false);
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				searchLocation();
			}
		});
	}	
	
	private void searchLocation() {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		String result = dialog.open();
		if (result != null) {
			locationText.setText(result);
		}		
	}
	
}
