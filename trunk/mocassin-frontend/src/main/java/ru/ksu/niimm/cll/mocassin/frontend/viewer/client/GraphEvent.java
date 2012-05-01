/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import com.google.gwt.event.shared.GwtEvent;

public class GraphEvent extends GwtEvent<GraphEventHandler> {
	public static final Type<GraphEventHandler> TYPE = new Type<GraphEventHandler>();

	private final Node currentNode;

	public GraphEvent(Node currentNode) {
		super();
		this.currentNode = currentNode;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GraphEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GraphEventHandler handler) {
		handler.onNodeChange(this);
	}

}
