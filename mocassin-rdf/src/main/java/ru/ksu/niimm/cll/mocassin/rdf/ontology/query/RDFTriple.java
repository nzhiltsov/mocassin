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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.query;

public interface RDFTriple {
	/**
	 * 
	 * returns RDF triple representation in the N3 format, e.g.
	 * 
	 * <pre>
	 * &quot;&lt;all.omdoc#whatislogic&gt; &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#type&gt; &lt;http://omdoc.org/ontology#Theory&gt; .&quot;
	 * </pre>
	 * 
	 * @return RDF triple representation in the N3 format
	 */
	String getValue();
}
