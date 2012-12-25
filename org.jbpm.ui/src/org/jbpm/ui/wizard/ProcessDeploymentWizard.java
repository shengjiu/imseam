package org.jbpm.ui.wizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.jbpm.ui.util.ProcessDeployerOld;

public class ProcessDeploymentWizard extends Wizard {
	
	IFolder parFolder;
	ProcessDeploymentWizardPage mainPage;
	
	public void init(IWorkbench w, IStructuredSelection currentSelection) {
		setNeedsProgressMonitor(true);
		setWindowTitle("Process Deployment");
		parFolder = (IFolder) currentSelection.getFirstElement();
	}

	public void addPages() {
		super.addPages();
		mainPage = new ProcessDeploymentWizardPage();
		this.addPage(mainPage);
	}	
	
	public boolean performFinish() {
		return createProcessDeployer().deploy();
	}
	
	private ProcessDeployerOld createProcessDeployer() {
		String location = null;
		boolean saveParFile = mainPage.saveLocallyCheckbox.getSelection();
		if (saveParFile) {
			location = mainPage.locationText.getText();
		}
		return new ProcessDeployerOld(
				getContainer().getShell(),
				parFolder,
				mainPage.serverNameText.getText(),
				mainPage.serverPortText.getText(),
				mainPage.deployerText.getText(),
				location
			);
	}

}
