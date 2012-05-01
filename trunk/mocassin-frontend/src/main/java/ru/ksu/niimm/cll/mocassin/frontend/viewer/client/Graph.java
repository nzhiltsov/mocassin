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

import java.io.Serializable;

import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.protovis.LinkAdapter;

@SuppressWarnings("serial")
public class Graph implements Serializable {
	private Node[] nodes;
	private LinkAdapter[] links;

	public Graph() {
	}

	public Graph(Node[] nodes, LinkAdapter[] links) {
		this.nodes = nodes;
		this.links = links;
	}

	public Node[] getNodes() {
		return nodes;
	}

	public LinkAdapter[] getLinks() {
		return links;
	}

}
