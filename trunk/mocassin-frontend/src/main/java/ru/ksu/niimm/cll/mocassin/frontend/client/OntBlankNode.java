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
package ru.ksu.niimm.cll.mocassin.frontend.client;

@SuppressWarnings("serial")
public class OntBlankNode extends OntElement {

	public OntBlankNode() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof OntBlankNode ? true : false;
	}

}
