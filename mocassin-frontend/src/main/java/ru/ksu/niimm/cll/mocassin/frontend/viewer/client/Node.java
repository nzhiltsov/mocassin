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

import org.thechiselgroup.choosel.protovis.client.PVNodeAdapter;

@SuppressWarnings("serial")
public class Node implements Serializable {
	private String uri;

	private String name;

	private int nodeType;

	private int numPage;

	public Node() {
	}

	public Node(String uri, String name, int type) {
		this.uri = uri;
		this.name = name;
		this.nodeType = type;
	}

	public String getName() {
		return name;
	}

	public int getNodeType() {
		return nodeType;
	}

	public int getNumPage() {
		return numPage;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	public static class NovelCharacterNodeAdapter implements
			PVNodeAdapter<Node> {

		public String getNodeName(Node t) {
			return t.getName();
		}

		public Object getNodeValue(Node t) {
			return t.getNodeType();
		}
	}

}
