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

/**
 * The reference searcher builds a structural graph for a given GATE document.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface ReferenceSearcher {
    /**
     * For a given document, this method retrieves its structural elements and
     * references between them in the form of the graph
     * 
     * @param document
     *            GATE document
     * @param paperUrl
     *            document URI, e.g. 'http://mathnet.ru/ivm18'
     * @returns a graph that contains structural elements as nodes and
     *          references as edges
     */
    Graph<StructuralElement, Reference> retrieveStructuralGraph(
	    Document document, String paperUrl);
}
