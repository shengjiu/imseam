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
package org.jbpm.ui.prefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.jbpm.ui.DesignerLogger;
import org.jbpm.ui.DesignerPlugin;
import org.jbpm.ui.PluginConstants;

public class PreferencesManager implements PluginConstants {
	
	public static final PreferencesManager INSTANCE = new PreferencesManager();
	
	private Map jbpmInstallations;
	private File installationsFile;
		
	private PreferencesManager() {
		if (INSTANCE == null) {
			getPreferences().setDefault(JBPM_NAME, DEFAULT_JBPM_NAME);
		}
		initializeInstallations();
	}
	
	public void setDefault() {
		jbpmInstallations = new HashMap();
		jbpmInstallations.put(
				getPreferences().getDefaultString(JBPM_NAME), 
				getDefaultJbpmInstallation());
	}
	
	private String getDefaultJbpmLocationValue() {
		try { 		
			return Platform.asLocalURL(Platform.getBundle("org.jbpm.core").getEntry("/")).getFile().toString();
		} 
		catch (IOException ioe) { 
			DesignerLogger.logError("Exception while gettint default jBPM installation", ioe); 
		} 
		return null; 
	}
	
	private JbpmInstallation getDefaultJbpmInstallation() {
		JbpmInstallation result = new JbpmInstallation();
		result.name = getPreferences().getDefaultString(JBPM_NAME);
		result.location = getDefaultJbpmLocationValue();
		try {
			URL url = Platform.getBundle("org.jbpm.core").getEntry("/src/resources/gpd/version.info.xml");
			if (url != null) {
				Document document = new SAXReader().read(url);
				result.version = document.getRootElement().attribute("name").getValue();
			}			
			
		} catch (DocumentException e) {}
		return result;
	}
	
	private static Preferences getPreferences() {
		return DesignerPlugin.getDefault().getPluginPreferences();
	}
	
	private void initializeInstallations() {
		installationsFile = 
			DesignerPlugin.getDefault().getStateLocation().append("jbpm-installations.xml").toFile();
		if (!installationsFile.exists()) {
			createInstallationsFile();
		} else {
			loadInstallations();
		}
	}

	private void createInstallationsFile() {
		try {
			installationsFile.createNewFile();
			setDefault();
			saveInstallations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadInstallations() {
		Reader reader = null;
		jbpmInstallations = new HashMap();
		try {
			reader = new FileReader(installationsFile);
			XMLMemento memento = XMLMemento.createReadRoot(reader);
			IMemento[] children = memento.getChildren("installation");
			for (int i = 0; i < children.length; i++) {
				JbpmInstallation installation = new JbpmInstallation();
				installation.name = children[i].getString("name");
				installation.location = children[i].getString("location");
				installation.version = children[i].getString("version");				
				jbpmInstallations.put(installation.name, installation);
			}
		} catch (WorkbenchException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveInstallations() {
		XMLMemento memento = XMLMemento.createWriteRoot("installations");
		Iterator iterator = jbpmInstallations.keySet().iterator();
		while (iterator.hasNext()) {
			String name = (String)iterator.next();
			JbpmInstallation installation = (JbpmInstallation)jbpmInstallations.get(name);
			IMemento child = memento.createChild("installation");
			child.putString("name", installation.name);
			child.putString("location", installation.location);
			child.putString("version", installation.version);
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(installationsFile);
			memento.save(writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Map getJbpmInstallationMap() {
		return jbpmInstallations;
	}
	
	public JbpmInstallation getJbpmInstallation(String name) {
		return (JbpmInstallation)jbpmInstallations.get(name);
	}
	
	public String getPreferredJbpmName() {
		return getPreferences().getString(JBPM_NAME);
	}
	
}
