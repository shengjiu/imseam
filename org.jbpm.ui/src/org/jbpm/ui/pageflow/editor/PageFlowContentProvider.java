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
package org.jbpm.ui.pageflow.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.jbpm.ui.DesignerPlugin;
import org.jbpm.ui.model.Bendpoint;
import org.jbpm.ui.model.Node;
import org.jbpm.ui.model.ProcessDefinition;
import org.jbpm.ui.model.Transition;

public class PageFlowContentProvider {

	public static final PageFlowContentProvider INSTANCE = new PageFlowContentProvider();

	
	// Writing the stuff
	
	public void saveToInput(
			IEditorInput input,
			ProcessDefinition processDefinition) {
		try {
			getGpdFile(((IFileEditorInput)input).getFile()).setContents(
					new ByteArrayInputStream(toGraphicsXml(processDefinition).getBytes()), true, true, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }	
		
	private String toGraphicsXml(ProcessDefinition processDefinition) {
		StringWriter writer = new StringWriter();
		write(processDefinition, writer);
		return writer.toString();
	}

	private void write(
			ProcessDefinition processDefinition, Writer writer) {
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("pageflow-diagram");
			write(processDefinition, root);
			XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
			xmlWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace(new PrintWriter(writer));
		}
	}
	
	private void write(
			ProcessDefinition processDefinition,
			Element element) {
		addAttribute(element, "name", processDefinition.getName());
		addAttribute(element, "width", Integer.toString(processDefinition.getDimension().width));
		addAttribute(element, "height", Integer.toString(processDefinition.getDimension().height));
		Iterator iter = processDefinition.getNodes().iterator();
		while (iter.hasNext()) {
			Node node = (Node) iter.next();
			write(node, addElement(element, "node"));
		}
	}
	
	private void write(Node node, Element element) {
		addAttribute(element, "name", node.getName());
		addAttribute(element, "x", String.valueOf(node.getConstraint().x));
		addAttribute(element, "y", String.valueOf(node.getConstraint().y));
		addAttribute(element, "width", String.valueOf(node.getConstraint().width));
		addAttribute(element, "height", String.valueOf(node.getConstraint().height));
		Iterator transitions = node.getLeavingTransitions().iterator();
		while (transitions.hasNext()) {
			Transition transition = (Transition) transitions.next();
			write(transition, addElement(element, "transition"));
		}
	}

	private void write(Transition transition,
			Element element) {
		String name = transition.getName();
		if (name != null) {
			addAttribute(element, "name", name);
		}
		Point offset = transition.getLabel().getOffset();
		if (offset != null) {
			Element label = addElement(element, "label");
			addAttribute(label, "x", String.valueOf(offset.x));
			addAttribute(label, "y", String.valueOf(offset.y));
		}
		Iterator bendpoints = transition.getBendpoints().iterator();
		while (bendpoints.hasNext()) {
			write((Bendpoint) bendpoints.next(), addElement(element, "bendpoint"));
		}
	}

	private void write(Bendpoint bendpoint, Element bendpointElement) {
		addAttribute(bendpointElement, "w1", String.valueOf(bendpoint
				.getFirstRelativeDimension().width));
		addAttribute(bendpointElement, "h1", String.valueOf(bendpoint
				.getFirstRelativeDimension().height));
		addAttribute(bendpointElement, "w2", String.valueOf(bendpoint
				.getSecondRelativeDimension().width));
		addAttribute(bendpointElement, "h2", String.valueOf(bendpoint
				.getSecondRelativeDimension().height));
	}
	
	private Element addElement(Element element, String elementName) {
		Element newElement = element.addElement(elementName);
		return newElement;
	}

	private void addAttribute(Element e, String attributeName,
			String value) {
		if (value != null) {
			e.addAttribute(attributeName, value);
		}
	}

	// Reading the stuff

	public FileEditorInput getJpdlEditorInput(IEditorInput input) {
		return new FileEditorInput(getJpdlFile(((IFileEditorInput)input).getFile()));
	}
	
	public FileEditorInput getGpdEditorInput(IEditorInput input) {
		return new FileEditorInput(getGpdFile(((IFileEditorInput)input).getFile()));
	}
	
	private IFile getJpdlFile(IFile gpdFile) {
		IProject project = gpdFile.getProject();
		IPath gpdPath = gpdFile.getProjectRelativePath();
		IPath jpdlPath = gpdPath.removeLastSegments(1).append(getJpdlFileName(gpdFile));
		IFile jpdlFile = project.getFile(jpdlPath);
		return jpdlFile;
	}
	
	private String getJpdlFileName(IFile gpdFile) {
		return gpdFile.getName().substring(5);
	}

	private IFile getGpdFile(IFile jpdlFile) {
		IProject project = jpdlFile.getProject();
		IPath jpdlPath = jpdlFile.getProjectRelativePath();
		IPath gpdPath = jpdlPath.removeLastSegments(1).append(getGpdFileName(jpdlFile));
		IFile gpdFile = project.getFile(gpdPath);
		return gpdFile;
	}
	
	private String getGpdFileName(IFile jpdlFile) {
		return ".gpd." + jpdlFile.getName();
	}

	public void addGraphicalInfo(ProcessDefinition processDefinition,
			IEditorInput input) throws PartInitException {
		try {
			IFile file = ((IFileEditorInput)input).getFile();
			if (file.exists()) {
				addGraphicalInfo(processDefinition,
					new InputStreamReader(((IFileEditorInput)input).getFile().getContents()));
			} else {
				file.create(initialGraphicalInfo(), true, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
			throw new PartInitException(
					new Status(IStatus.ERROR, DesignerPlugin.getDefault()
							.getClass().getName(), 0,
							"Error reading contents of file : "
									+ input.getName() + ".", null));
		}
	}
	
	private ByteArrayInputStream initialGraphicalInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n\n<pageflow-diagram />");
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}

	private void addGraphicalInfo(
			ProcessDefinition processDefinition, Reader reader) {
		try {
			Element processDiagramInfo = new SAXReader().read(reader)
					.getRootElement();
			addGraphicalInfo(processDefinition, processDiagramInfo);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void addGraphicalInfo(
			ProcessDefinition processDefinition,
			Element processDiagramInfo) {
		addProcessDiagramDimension(processDefinition, processDiagramInfo);
		Iterator nodeIterator = processDiagramInfo.elements("node").iterator();
		while (nodeIterator.hasNext()) {
			Element nodeInfo = (Element) nodeIterator.next();
			Node node = processDefinition.getNodeByName(nodeInfo.attributeValue("name"));
			addGraphicalInfo(node, nodeInfo);
		}
	}
	
	private void addProcessDiagramDimension(
			ProcessDefinition processDefinition, 
			Element processDiagramInfo) {
		String width = processDiagramInfo.attributeValue("width");
		String height = processDiagramInfo.attributeValue("height");
		Dimension dimension = new Dimension(
			width == null ? 0 : Integer.valueOf(width).intValue(),
			height == null ? 0 : Integer.valueOf(height).intValue());
		processDefinition.setDimension(dimension);
	}

	private void addGraphicalInfo(Node node, Element nodeInfo) {
		Rectangle constraint = new Rectangle();
		constraint.x = Integer.valueOf(nodeInfo.attributeValue("x")).intValue();
		constraint.y = Integer.valueOf(nodeInfo.attributeValue("y")).intValue();
		constraint.width = Integer.valueOf(nodeInfo.attributeValue("width")).intValue();
		constraint.height = Integer.valueOf(nodeInfo.attributeValue("height")).intValue();
		node.setConstraint(constraint);
		List leavingTransitions = node.getLeavingTransitions();
		List transitionInfoList = nodeInfo.elements("transition");
		for (int i = 0; i < leavingTransitions.size(); i++) {
			addGraphicalInfo(
					(Transition)leavingTransitions.get(i), 
					(Element)transitionInfoList.get(i));
		}
	}
	
	private void addGraphicalInfo(Transition transition, Element transitionInfo) {
		Element label = transitionInfo.element("label");
		if (label != null) {
			Point offset = new Point();
			offset.x = Integer.valueOf(label.attributeValue("x")).intValue();
			offset.y = Integer.valueOf(label.attributeValue("y")).intValue();
			transition.getLabel().setOffset(offset);
		}
		Iterator bendpointIterator = transitionInfo.elements("bendpoint").iterator();
		List bendpoints = new ArrayList();
		while (bendpointIterator.hasNext()) {
			Element bendpointInfo = (Element)bendpointIterator.next();
			bendpoints.add(createBendpoint(bendpointInfo)); 
		}
		transition.setBendpoints(bendpoints);
	}
	
	private Bendpoint createBendpoint(Element bendpointInfo) {
		Bendpoint result = new Bendpoint();
		int w1 = Integer.valueOf(bendpointInfo.attributeValue("w1")).intValue();
		int h1 = Integer.valueOf(bendpointInfo.attributeValue("h1")).intValue();
		int w2 = Integer.valueOf(bendpointInfo.attributeValue("w2")).intValue();
		int h2 = Integer.valueOf(bendpointInfo.attributeValue("h2")).intValue();
		Dimension d1 = new Dimension(w1, h1);
		Dimension d2 = new Dimension(w2, h2);
		result.setRelativeDimensions(d1, d2);
		return result;
	}
	
}
