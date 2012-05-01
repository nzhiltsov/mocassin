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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.TokenImpl;

import com.thoughtworks.xstream.XStream;

public class ReferenceXStream extends XStream {

	private static final String DOCUMENT_TAG_NAME = "document";
	private static final String TOKEN_TAG_NAME = "token";
	private static final String REFERENCE_TAG_NAME = "reference";

	public ReferenceXStream() {
		super();
		alias(REFERENCE_TAG_NAME, ReferenceImpl.class);
		alias(TOKEN_TAG_NAME, TokenImpl.class);
		alias(DOCUMENT_TAG_NAME, ParsedDocumentImpl.class);
	}

}
