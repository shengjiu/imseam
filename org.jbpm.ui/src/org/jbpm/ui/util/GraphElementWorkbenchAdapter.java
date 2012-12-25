package org.jbpm.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.jbpm.ui.model.GraphElement;

public class GraphElementWorkbenchAdapter implements IWorkbenchAdapter {
	
	GraphElement graphElement;
	
	public GraphElementWorkbenchAdapter(GraphElement graphElement) {
		this.graphElement = graphElement;
	}

	public Object[] getChildren(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel(Object o) {
		return graphElement.getName();
	}

	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
