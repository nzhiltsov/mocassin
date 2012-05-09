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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;

public interface StructuralElementTypeRecognizer {
	

	/**
	 * predicts which type given element has
	 * 
	 * @param structuralElement
	 * @return
	 */
	MocassinOntologyClasses predict(StructuralElement structuralElement);

}
