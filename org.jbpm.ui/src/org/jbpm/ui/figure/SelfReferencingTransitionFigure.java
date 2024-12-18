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
package org.jbpm.ui.figure;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.PointList;

public class SelfReferencingTransitionFigure extends PolylineConnection {
	
	public SelfReferencingTransitionFigure() {
		System.out.println("creating self ref transition.");
		createConnection();
		createDecoration();
	}
	
	public void setPoints(PointList points) {
		System.out.println("Setting points");
		super.setPoints(points);
	}
	
	private void createConnection() {
		PointList points = new PointList(4);
		points.addPoint(0, 10);
		points.addPoint(-10, 10);
		points.addPoint(-10, -10);
		points.addPoint(0, 0);
		setPoints(points);
	}

	private void createDecoration() {
		PolygonDecoration arrow = new PolygonDecoration();
		arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		arrow.setScale(5, 2.5);
		setTargetDecoration(arrow);
	}
	
}
