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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.util.List;

/**
 * Annotation of semantics of a reference.
 * <p>
 * A reference is characterized by so-called 'from' and 'to' elements to define
 * direction in a relationship.
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceContext {
	/**
	 * 
	 * @return id of 'to' element
	 */
	String getTo();

	void setTo(String to);
	/**
	 * 
	 * @return id of 'from' element
	 */
	String getFrom();

	void setFrom(String from);

	String getFilename();

	void setFilename(String filename);

	String getRefid();

	void setRefid(String refid);

	List<Feature> getFeatures();

	void setFeatures(List<Feature> features);
}
