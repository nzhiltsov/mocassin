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
/**
 * This class recognizes the types of given structural elements.
 * 
 * @author Nikita Zhiltsov
 *
 */
public interface StructuralElementTypeRecognizer {
	

	/**
	 * Recognizes the type  of a given structural element
	 * 
	 * @param structuralElement structural element
	 * @returns class from the Mocassin Ontology
	 */
	MocassinOntologyClasses recognize(StructuralElement structuralElement);

}
