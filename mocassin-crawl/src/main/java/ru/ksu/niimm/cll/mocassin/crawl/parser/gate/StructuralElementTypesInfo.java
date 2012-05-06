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

import java.util.SortedMap;

/**
 * Prediction model for a pair of structural element types based on string
 * similarity techniques
 * 
 * @author nzhiltsov
 * 
 */
public interface StructuralElementTypesInfo {
	/**
	 * return a reference associated with info
	 * 
	 * @return
	 */
	Reference getReference();

	/**
	 * return structural type similarity vector for 'from' element of a reference
	 * 
	 * @return
	 */
	SortedMap<String, Float> getFromElementVector();

	/**
	 * return structural type similarity vector for 'to' element of a reference
	 * 
	 * @return
	 */
	SortedMap<String, Float> getToElementVector();
}
