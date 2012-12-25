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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.jbpm.ui.PluginConstants;

public class HandlerConfigurationComposite extends Composite implements PluginConstants {
	
	HandlerMainInfoComposite handlerMainInfoComposite;
	HandlerDetailsComposite handlerDetailsComposite;
	
	Map initialValues;
	boolean detailsShown = false;
	String type, title, message;
	
	public HandlerConfigurationComposite(
			Composite parent, String type, String title, String message, Map initialValues) {
		super(parent, SWT.NONE);
		initialize(type, title, message, initialValues);
		setLayout(new GridLayout(1, false));
		createHandlerMainComposite();
		createSeparator();
		updateControl();
	}
	
	private void initialize(String t, String str, String m, Map initVal) {
		this.type = t;
		this.title = str;
		this.message = m;
		this.initialValues = initVal;
	}

	private void createSeparator() {
		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorData = new GridData(GridData.FILL_HORIZONTAL);
		separatorData.heightHint = 10;
		separator.setLayoutData(separatorData);
	}
	
	private void createHandlerMainComposite() {
		handlerMainInfoComposite = new HandlerMainInfoComposite(
				this, type, title, message, initialValues);
		GridData handlerInfoCompositeData = new GridData(GridData.FILL_HORIZONTAL);
		handlerMainInfoComposite.setLayoutData(handlerInfoCompositeData);
		handlerMainInfoComposite.setConfigTypeListener(new Listener() {
			public void handleEvent(Event event) {
				Combo combo = (Combo)event.widget;
				handlerDetailsComposite.setConfigType(combo.getItem(combo.getSelectionIndex()));
			}			
		});
		handlerMainInfoComposite.setClassTextListener(new Listener() {
			public void handleEvent(Event event) {
				Text text = (Text)event.widget;
				handlerDetailsComposite.setHandlerClass(text.getText());
			}
		});
	}
	
	private void createHandlerDetailsComposite() {
		handlerDetailsComposite = new HandlerDetailsComposite(this, initialValues);
		GridData handlerDetailsCompositeData = new GridData(GridData.FILL_HORIZONTAL);
		handlerDetailsComposite.setLayoutData(handlerDetailsCompositeData);
	}
	
	private void deleteHandlerDetailsComposite() {
		if (handlerDetailsComposite != null) {
			initialValues.put(
					"bean", handlerDetailsComposite.getBeanConfigurationElements());
			initialValues.put(
					"field", handlerDetailsComposite.getFieldConfigurationElements());
			initialValues.put(
					"constructor", handlerDetailsComposite.getConstructorConfigurationString());
			initialValues.put(
					"compatibility", handlerDetailsComposite.getCompatibilityConfigurationString());
			handlerDetailsComposite.dispose();
		}
	}
	
	private void updateControl() {
		handlerMainInfoComposite.setEnabled(detailsShown);
		if (detailsShown) {
			createHandlerDetailsComposite();
		} else {
			deleteHandlerDetailsComposite();
		}
		getParent().layout();
	}
	
	public void showDetails(boolean showDetails) {
		this.detailsShown = showDetails;
		updateControl();
	}
	
	public String getClassName() {
		String result = handlerMainInfoComposite.getHandlerClassName();
		return "".equals(result) ? null : result;
	}
	
	public String getConfigType() {
		return handlerMainInfoComposite.getConfigType();
	}
	
	public Object getConfigData() {
		if (CONSTRUCTOR_CONFIG_TYPE.equals(getConfigType())) {
			return handlerDetailsComposite.getConstructorConfigurationString();
		} else if (COMPATIBILITY_CONFIG_TYPE.equals(getConfigType())) {
			return handlerDetailsComposite.getCompatibilityConfigurationString();
		} else if (BEAN_CONFIG_TYPE.equals(getConfigType())) {
			return handlerDetailsComposite.getBeanConfigurationElements();
		} else {
			return handlerDetailsComposite.getFieldConfigurationElements();
		}
	}
	
}
