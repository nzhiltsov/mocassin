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

import edu.uci.ics.jung.graph.Graph;
import gate.Document;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;

/**
 * Represents functionality of searching the structural elements in GATE
 * documents
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface StructuralElementSearcher {
    /**
     * This method retrieves all the structural elements of a given document
     * 
     * @param document
     *            GATE document to be analyzed
     * @param paperUrl
     *            paper URL, e.g. 'http://mathnet.ru/ivm18'
     * @returns list of structural elements
     */
    List<StructuralElement> retrieveElements(Document document, String paperUrl);

    /**
     * 
     * This method finds the closest element, which is followed by the given
     * element, checking its type using given domains.
     * 
     * @param element
     *            given element
     * @param validDomains
     *            domains to filter
     * @param graph
     *            graph that contains both the elements
     * @returns closest preceding structural element, which is suffices given
     *          restrictions
     */
    StructuralElement findClosestPredecessor(StructuralElement element,
	    MocassinOntologyClasses[] validDomains,
	    Graph<StructuralElement, Reference> graph);
}
