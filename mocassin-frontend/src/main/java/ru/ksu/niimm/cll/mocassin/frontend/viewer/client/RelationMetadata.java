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

public class RelationMetadata {
	private final Relations type;
	private final Node from;
	private final Node to;

	public RelationMetadata(Node from, Node to, Relations type) {
		this.from = from;
		this.to = to;
		this.type = type;
	}

	public Relations getType() {
		return type;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

}
