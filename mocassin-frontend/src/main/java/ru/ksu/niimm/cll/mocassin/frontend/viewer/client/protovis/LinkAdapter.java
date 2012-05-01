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
package ru.ksu.niimm.cll.mocassin.frontend.viewer.client.protovis;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LinkAdapter implements Serializable {
	private int source;

	private int target;

	private double value;

	private int type;

	public LinkAdapter() {
	}

	public LinkAdapter(int source, int target, double value, int type) {
		this.source = source;
		this.target = target;
		this.value = value;
		this.type = type;
	}

	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}

	public double getValue() {
		return value;
	}

	public int getType() {
		return type;
	}

}
