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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.jbpm.ui.DesignerLogger;
import org.jbpm.ui.prefs.JbpmInstallation;
import org.jbpm.ui.prefs.PreferencesManager;
import org.jbpm.ui.util.JbpmClasspathContainer;

public class NewProcessProjectWizard extends Wizard implements INewWizard {

	private WizardNewProjectCreationPage mainPage;
	private NewProcessProjectDetailsWizardPage coreJbpmPage;
	private IProject newProject;
	private IWorkbench workbench;
	   
	public void init(IWorkbench w, IStructuredSelection currentSelection) {
	    this.workbench = w;
		setNeedsProgressMonitor(true);
		setWindowTitle("New Process Project");
	}

	public void addPages() {
		super.addPages();
		setWindowTitle("New Process Project");
		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage");
		mainPage.setTitle("Process Project");
		mainPage.setDescription("Create a new process project.");
		coreJbpmPage = new NewProcessProjectDetailsWizardPage();
		this.addPage(mainPage);
		this.addPage(coreJbpmPage);
	}
	
	private IProject createNewProject() {
		final IProject newProjectHandle = mainPage.getProjectHandle();
		final IProjectDescription description = createProjectDescription(newProjectHandle);
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(description, newProjectHandle, monitor);
			}
		};
		runProjectCreationOperation(op, newProjectHandle);
		return newProjectHandle;
	}
	
	private void addJRELibraries(IJavaProject javaProject) throws JavaModelException {		
		ArrayList entries = new ArrayList();
		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		entries.addAll(Arrays.asList(PreferenceConstants.getDefaultJRELibrary()));
		javaProject.setRawClasspath((IClasspathEntry[])entries.toArray(new IClasspathEntry[entries.size()]), null);
	}
	
	private void addSourceFolders(IJavaProject javaProject) throws JavaModelException, CoreException {
		ArrayList entries = new ArrayList();
		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		addSourceFolder(javaProject, entries, "src/java");
//		addSourceFolder(javaProject, entries, "src/process");
		addSourceFolder(javaProject, entries, "src/config.files");
		addSourceFolder(javaProject, entries, "test/java");
		javaProject.setRawClasspath((IClasspathEntry[])entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

	private void addSourceFolder(IJavaProject javaProject, ArrayList entries, String path) throws CoreException {
		IFolder folder = javaProject.getProject().getFolder(path);
		createFolder(folder);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
		entries.add(JavaCore.newSourceEntry(root.getPath()));
	}
	
	private void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent != null && !parent.exists() && parent instanceof IFolder) {
			createFolder((IFolder)parent);
		}
		folder.create(true, true, null);
	}
	
	private JbpmInstallation getJbpmInstallation() {
		return PreferencesManager.INSTANCE.getJbpmInstallation(getCoreJbpmName());
	}
	
	private void createJbpmLibraryContainer(IJavaProject javaProject) throws JavaModelException {
		JavaCore.setClasspathContainer(
				new Path("JBPM/" + getJbpmInstallation().name),
				new IJavaProject[] { javaProject },
				new IClasspathContainer[] { new JbpmClasspathContainer(javaProject, getJbpmInstallation()) },
				null);		
	}
	
	private String getCoreJbpmName() {
		return coreJbpmPage.getCoreJbpmName();
	}
	
	private void addJbpmLibraries(IJavaProject javaProject) throws JavaModelException {
		createJbpmLibraryContainer(javaProject);
		ArrayList entries = new ArrayList();
		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		entries.add(JavaCore.newContainerEntry(new Path("JBPM/" + getJbpmInstallation().name)));
		javaProject.setRawClasspath((IClasspathEntry[])entries.toArray(new IClasspathEntry[entries.size()]), null);
	}
	
	private void createOutputLocation(IJavaProject javaProject) throws JavaModelException, CoreException {
		IFolder binFolder = javaProject.getProject().getFolder("bin");
		createFolder(binFolder);
		IPath outputLocation = binFolder.getFullPath();
		javaProject.setOutputLocation(outputLocation, null);
	}
	
	private void addJavaBuilder(IJavaProject javaProject) throws CoreException {
		IProjectDescription desc = javaProject.getProject().getDescription();
		ICommand command = desc.newCommand();
		command.setBuilderName(JavaCore.BUILDER_ID);
		desc.setBuildSpec(new ICommand[] { command });
		javaProject.getProject().setDescription(desc, null);
	}
	
	private void createJavaProject() { 
		try {
			newProject = createNewProject();
			newProject.setPersistentProperty(new QualifiedName("", "jbpmName"), getCoreJbpmName());
			IJavaProject javaProject = JavaCore.create(newProject);
			createOutputLocation(javaProject);
			addJavaBuilder(javaProject);
			setClasspath(javaProject);
			createInitialContent(javaProject);
			newProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
		} catch (JavaModelException e) {
			ErrorDialog.openError(getShell(), "Problem creating java project", null, e.getStatus());			
		} catch (CoreException e) {
			ErrorDialog.openError(getShell(), "Problem creating java project", null, e.getStatus());						
		} catch (IOException e) {
			ErrorDialog.openError(getShell(), "Problem creating java project", null, null);									
		}
	}
	
	private void createInitialContent(IJavaProject javaProject) throws CoreException, JavaModelException, IOException {
		if (coreJbpmPage.checkbox.getSelection()) {
			createMessageActionHandler(javaProject);
			createSimpleProcessTest(javaProject);
			createSimpleProcessDefinition(javaProject);
		}
		copyJbpmResources(javaProject);
	}
	
	private void createSimpleProcessDefinition(IJavaProject javaProject) throws CoreException, JavaModelException, IOException {
		JbpmInstallation jbpmInstallation = PreferencesManager.INSTANCE.getJbpmInstallation(getCoreJbpmName());
		if (jbpmInstallation == null) return;
		String jbpmLocation = jbpmInstallation.location;
		IFolder processesFolder = javaProject.getProject().getFolder("processes");
		if (!processesFolder.exists()) {
			processesFolder.create(true, true, null);
		}
		IFolder folder = javaProject.getProject().getFolder("processes/simple");
		if (!folder.exists()) {
			folder.create(true, true, null);
		}
		String fromPath = new Path(jbpmLocation).append("/src/process.examples/simple.par").toOSString();
		File fromDir = new File(fromPath);
		if (!fromDir.exists()) return;
		File[] files = fromDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			copyJbpmResource(files[i], folder);
		}
	}

	private void createSimpleProcessTest(IJavaProject javaProject) throws JavaModelException, IOException {
		String resourceName = "org/jbpm/ui/resource/SimpleProcessTest.java.template";
		IFolder folder = javaProject.getProject().getFolder("test/java");
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
		IPackageFragment pack = root.createPackageFragment("com.sample", true, null);
		InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
		byte[] content = readStream(stream);
		pack.createCompilationUnit("SimpleProcessTest.java", new String(content), true, null);
	}

	private void createMessageActionHandler(IJavaProject javaProject) throws JavaModelException, IOException {
		String resourceName = "org/jbpm/ui/resource/MessageActionHandler.java.template";
		IFolder folder = javaProject.getProject().getFolder("src/java");
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
		IPackageFragment pack = root.createPackageFragment("com.sample.action", true, null);
		InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
		byte[] content = readStream(stream);
		pack.createCompilationUnit("MessageActionHandler.java", new String(content), true, null);
	}
	
	private void copyJbpmResources(IJavaProject javaProject) throws CoreException {
		JbpmInstallation jbpmInstallation = PreferencesManager.INSTANCE.getJbpmInstallation(getCoreJbpmName());
		if (jbpmInstallation == null) return;
		String jbpmLocation = jbpmInstallation.location;
		IFolder folder = javaProject.getProject().getFolder("src/config.files");
		String fromPath = new Path(jbpmLocation).append("/src/config.files").toOSString();
		File fromDir = new File(fromPath);
		if (!fromDir.exists()) return;
		File[] files = fromDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			copyJbpmResource(files[i], folder);
		}
	}
	
	private void copyJbpmResource(File source, IFolder destination) throws CoreException {
		try {
			IFile file = destination.getFile(source.getName());
			file.create(new FileInputStream(source), true, null);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setClasspath(IJavaProject javaProject) throws JavaModelException, CoreException {
		javaProject.setRawClasspath(new IClasspathEntry[0], null);
		addSourceFolders(javaProject);
		addJRELibraries(javaProject);
		addJbpmLibraries(javaProject);
		// Hack to overcome the problems of the classpath container not being created in the classpath.
		javaProject.getRawClasspath();
	}

	private IProjectDescription createProjectDescription(
			IProject newProjectHandle) {
		IPath newPath = null;
		if (!mainPage.useDefaults())
			newPath = mainPage.getLocationPath();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProjectDescription description = workspace
				.newProjectDescription(newProjectHandle.getName());
		description.setLocation(newPath);
		addJavaNature(description);
		return description;
	}
	
	private void addJavaNature(IProjectDescription description) {
		ArrayList natures = new ArrayList();
		natures.addAll(Arrays.asList(description.getNatureIds()));
		natures.add(JavaCore.NATURE_ID);
		description.setNatureIds((String[])natures.toArray(new String[natures.size()]));
	}

	private void runProjectCreationOperation(WorkspaceModifyOperation op,
			IProject newProjectHandle) {
		try {
			getContainer().run(false, true, op);
		} catch (InterruptedException e) {
			DesignerLogger.logError("InterruptedException while creating project", e);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				handleCoreException(newProjectHandle, (CoreException) t);
			} else {
				handleOtherProblem(t);
			}
		}
	}

	private void handleOtherProblem(Throwable t) {
		MessageDialog.openError(getShell(), "Creation Problems",
				"Internal error: " + t.getMessage());
	}

	private void handleCoreException(final IProject newProjectHandle,
			CoreException e) {
		if (e.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
			MessageDialog
					.openError(
							getShell(),
							"Creation Problems",
							"The underlying file system is case insensitive. There is an existing project which conflicts with '"
									+ newProjectHandle.getName() + "'.");
		} else {
			ErrorDialog.openError(getShell(), "Creation Problems", null, e
					.getStatus());
		}
	}

	void createProject(IProjectDescription description, IProject projectHandle,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {
			monitor.beginTask("", 2000);
			projectHandle.create(description, new SubProgressMonitor(monitor,
					1000));
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			projectHandle.open(IResource.BACKGROUND_REFRESH,
					new SubProgressMonitor(monitor, 1000));
		} finally {
			monitor.done();
		}
	}

	public IProject getNewProject() {
		return newProject;
	}

	public boolean performFinish() {
		if (getCoreJbpmName() == null || getJbpmInstallation() == null)
			return false;
		createJavaProject();
		if (newProject == null)
			return false;
		updatePerspective();
		selectAndReveal(newProject);
		return true;
	}

	protected void updatePerspective() {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page.findView("org.eclipse.ui.views.PropertySheet") == null) {
				page.showView("org.eclipse.ui.views.PropertySheet");
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	protected void selectAndReveal(IResource newResource) {
		selectAndReveal(newResource, workbench.getActiveWorkbenchWindow());
	}

    private void selectAndReveal(IResource resource,
	           IWorkbenchWindow window) {
		if (!inputValid(resource, window)) return;   
		Iterator itr = getParts(window.getActivePage()).iterator();
		while (itr.hasNext()) {
		    selectAndRevealTarget(
					window, 
					new StructuredSelection(resource), 
					getTarget((IWorkbenchPart)itr.next()));
		}
	}
	
	private boolean inputValid(IResource resource, IWorkbenchWindow window) {
		if (window == null || resource == null) return false;
		else if (window.getActivePage() == null) return false;
		else return true;
	}

	private void selectAndRevealTarget(IWorkbenchWindow window, final ISelection selection, ISetSelectionTarget target) {
		if (target == null) return;
		final ISetSelectionTarget finalTarget = target;
		window.getShell().getDisplay().asyncExec(new Runnable() {
		    public void run() {
		        finalTarget.selectReveal(selection);
		    }
		});
	}
	
	private ISetSelectionTarget getTarget(IWorkbenchPart part) {
        ISetSelectionTarget target = null;
        if (part instanceof ISetSelectionTarget) {
            target = (ISetSelectionTarget)part;
        }
        else {
            target = (ISetSelectionTarget)part.getAdapter(ISetSelectionTarget.class);
        }
		return target;		
	}

	private List getParts(IWorkbenchPage page) {
		List result = new ArrayList();
		addParts(result, page.getViewReferences());
		addParts(result, page.getEditorReferences());
		return result;
	}
	
	private void addParts(List parts, IWorkbenchPartReference[] refs) {
		for (int i = 0; i < refs.length; i++) {
           IWorkbenchPart part = refs[i].getPart(false);
           if (part != null) {
               parts.add(part);
           }
	    }		
	}
	
	private byte[] readStream(InputStream in) throws IOException {
		byte[] contents = null;
		int fileSize = 0;
		byte[] buffer = new byte[1024];
		int bytesRead = in.read(buffer);
		while (bytesRead != -1) {
			byte[] newContents = new byte[fileSize + bytesRead];
			if (fileSize > 0) {
				System.arraycopy(contents, 0, newContents, 0, fileSize);
			}
			System.arraycopy(buffer, 0, newContents, fileSize, bytesRead);
			contents = newContents;
			fileSize += bytesRead;
			bytesRead = in.read(buffer);
		}
		return contents;
	}
	
}