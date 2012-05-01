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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.location;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

/**
 * The interface represents characteristics related to mutual location of
 * structural elements of a given reference
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceElementLocationInfo {
	/**
	 * return reference for which the distances are computed
	 * 
	 * @return
	 */
	Reference getReference();

	/**
	 * distance between start points of reference's structural elements
	 * normalized by document length
	 * 
	 * @return
	 */
	float getStartDistance();

	/**
	 * distance between end points of reference's structural elements normalized
	 * by document length
	 * 
	 * @return
	 */
	float getEndDistance();
}
