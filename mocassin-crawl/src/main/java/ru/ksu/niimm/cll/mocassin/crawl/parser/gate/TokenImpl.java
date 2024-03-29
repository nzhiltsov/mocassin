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


public class TokenImpl implements Token {
	private String value;
	private String pos;
    private String stem;

	public TokenImpl(String value, String pos, String stem) {
		this.value = value;
		this.pos = pos;
        this.stem = stem;
	}

	public String getValue() {
		return value;
	}

    public String getStem() {
        return stem;
    }

	public String getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return value;
	}

}
