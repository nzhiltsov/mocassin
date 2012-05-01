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

/**
 * This interface represents information about a token
 * 
 * @author nzhiltsov
 * 
 */
public interface Token {
	/**
	 * returns string value of a token (or its stem - see configuration)
	 * 
	 * @return
	 */
	String getValue();

	/**
	 * returns POS tag according to GATE tag set: {@link http
	 * ://gate.ac.uk/sale/tao/splitap7.html}
	 * 
	 * @return
	 */
	String getPos();
}
