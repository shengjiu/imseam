package org.jbpm.ui.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.xpath.DefaultXPath;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jbpm.ui.DesignerLogger;
import org.jbpm.ui.DesignerPlugin;

public class ProcessDeployerOld {
	
	private static final String boundary = "AaB03x";
	
	Shell shell;
	IFolder parFolder;
	String serverName;
	String serverPort;
	String deployer;
	String targetLocation;
	ArrayList classesAndResources;
	ArrayList filesAndFolders;
	
	public ProcessDeployerOld() {
	}
	
	public ProcessDeployerOld(
			Shell shell,
			IFolder parFolder,
			String serverName, 
			String serverPort, 
			String deployer, 
			String targetLocation) {
		this.parFolder = parFolder;
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.deployer = deployer;
		this.targetLocation = targetLocation;
	}
	
	public boolean deploy() {
		try {
			showProgressMonitorDialog();
			showSuccessDialog();
			return true;
		} catch (Throwable t) {
			showErrorDialog(t);
			return false;
		}
	}
	
	private void showProgressMonitorDialog() throws Exception {
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
		progressMonitorDialog.run(false, false, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InterruptedException {
				try {
					IProject project = parFolder.getProject();
					IJavaProject javaProject = JavaCore.create(project);
					String[] pathArray = JavaRuntime
							.computeDefaultRuntimeClassPath(javaProject);
					URL[] urls = new URL[pathArray.length];
					for (int i = 0; i < pathArray.length; i++) {
						urls[i] = new File(pathArray[i]).toURL();
					}
					byte[] baos = createParBytes(urls);
					if (targetLocation != null) {
						saveParFile(baos);
					}
					deployProcessWithServlet(baos);
					return;
				} catch (Exception e) {
					DesignerLogger
							.logError(
									"Exception happened while deploying",
									e);
				}
				throw new InterruptedException(
						"Error while deploying, look in the Error Log for more info");
			}
		});
	}
	
	private void saveParFile(byte[] parBytes) throws IOException {
		File file = new Path(targetLocation).toFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(parBytes);
		fos.close();
	}

	private void deployProcessWithServlet(byte[] parBytes) throws Exception {
		URL url = new URL("http://" + serverName + ":" + serverPort + "/" + deployer);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestProperty("Content-Type",
				"multipart/form-data, boundary=AaB03x");
		DataOutputStream dataOutputStream = new DataOutputStream(urlConnection
				.getOutputStream());
		dataOutputStream.writeBytes("--" + boundary + "\r\n");
		dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"definition\"; filename=\"dummy.par\"\r\n");
		dataOutputStream.writeBytes("Content-Type: application/x-zip-compressed\r\n\r\n");
		
		dataOutputStream.write(parBytes);
		
		dataOutputStream.writeBytes("\r\n--" + boundary + "--\r\n");
		dataOutputStream.flush();
		dataOutputStream.close();
		InputStream inputStream = urlConnection.getInputStream();
		StringBuffer result = new StringBuffer();
		int read;
		while ((read = inputStream.read()) != -1) {
			result.append((char)read);
		}
	}
	
	private byte[] createParBytes(URL[] urls) throws Exception {
		URLClassLoader newLoader = new URLClassLoader(urls, getClass()
				.getClassLoader());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
		addFile(zipOutputStream, "processdefinition.xml");
		IFile imageFile = parFolder.getFile("processimage.jpg");
		if (imageFile.exists()) {
			addFile(zipOutputStream, "gpd.xml");
			addFile(zipOutputStream, "processimage.jpg");
		}
		addVersionedClasses(zipOutputStream, newLoader);
		zipOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	private void showSuccessDialog() {
		MessageDialog dialog = new MessageDialog(shell, "Deployment Successful", null,
				"The process archive deployed successfully.",
				SWT.ICON_INFORMATION, new String[] { "OK" }, 0);
		dialog.open();
	}

	private void showErrorDialog(Throwable t) {
		ErrorDialog dialog = new ErrorDialog(shell,
				"Unexpected Deployment Exception",
				"An exception happened during the deployment of the process",
				new Status(
						Status.ERROR,
						DesignerPlugin.getDefault().getBundle()
								.getSymbolicName(),
						Status.ERROR,
						"An unexpected exception caused the deployment to fail",
						t), Status.ERROR);
		dialog.open();
	}

	private void addVersionedClasses(ZipOutputStream zos, ClassLoader loader)
			throws CoreException, IOException {
		try {
			IFile file = parFolder.getFile("processdefinition.xml");
			HashSet classes = new HashSet();
			InputStreamReader reader = new InputStreamReader(file.getContents());
			Element processDefinitionInfo = new SAXReader().read(reader)
					.getRootElement();
			XPath xPath = new DefaultXPath("//@class"); 
			List list = xPath.selectNodes(processDefinitionInfo);
			for (int i = 0; i < list.size(); i++) {
				String className = ((Attribute) list.get(i)).getValue();
				if (!classes.contains(className)) {
					classes.add(className);
					addVersionedClass(zos, loader, className);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	private void addVersionedClass(ZipOutputStream zos, ClassLoader loader,
			String className) throws IOException {
		byte[] buff = new byte[256];
		String fileName = className.replace('.', '/') + ".class";
		zos.putNextEntry(new ZipEntry("classes/" + fileName));
		InputStream is = loader.getResourceAsStream(fileName);
		int read;
		while ((read = is.read(buff)) != -1) {
			zos.write(buff, 0, read);
		}
	}

	private void addFile(ZipOutputStream zos, String fileName)
			throws CoreException, IOException {
		byte[] buff = new byte[256];
		IFile file = parFolder.getFile(fileName);
		InputStream is = file.getContents();
		zos.putNextEntry(new ZipEntry(fileName));
		int read;
		while ((read = is.read(buff)) != -1) {
			zos.write(buff, 0, read);
		}
	}
}
