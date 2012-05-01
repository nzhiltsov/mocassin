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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.model;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public final class URIConstants {
	public static final URI RDF_TYPE = new URIImpl(
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

	private URIConstants() {
	}

	public static Statement createTriple(String subject, String predicate,
			String object) {
		return new StatementImpl(new URIImpl(subject), new URIImpl(predicate),
				new URIImpl(object));
	}

	public static Statement createRdfTypeTriple(String subject, String object) {
		return new StatementImpl(new URIImpl(subject), RDF_TYPE, new URIImpl(
				object));
	}

	public static Statement createLiteralTriple(String subject,
			String predicate, String literal) {
		return new StatementImpl(new URIImpl(subject), new URIImpl(predicate),
				new LiteralImpl(literal));
	}

	public static Statement createIntegerTriple(String subject,
			String predicate, int value) {
		return new StatementImpl(new URIImpl(subject), new URIImpl(predicate),
				new NumericLiteralImpl(value));
	}
}
