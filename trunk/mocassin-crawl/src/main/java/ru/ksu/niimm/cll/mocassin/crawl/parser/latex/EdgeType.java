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
package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

/**
 * Relation types
 * 
 * @author nzhiltsov
 * 
 */
public enum EdgeType {
	/**
	 * this relation type is about one node contains another, e.g. a section
	 * contains a proposition
	 */
	CONTAINS,
	/**
	 * this relation type concerns the relation between a reference and the
	 * label that it refers to
	 */
	REFERS_TO
}
