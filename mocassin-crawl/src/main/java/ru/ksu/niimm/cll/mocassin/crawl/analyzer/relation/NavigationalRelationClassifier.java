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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

/**
 * Navigational relation classifier implements predicting the relation type of
 * reference in the document graph
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface NavigationalRelationClassifier {
    /**
     * Predicts the relation type of a given reference
     * 
     * @param reference
     *            reference
     * @param graph
     *            a graph that contains a given reference
     * @returns an object that contains the most probable relation type and the
     *          prediction confidence score
     */
    Prediction predict(Reference reference,
	    Graph<StructuralElement, Reference> graph);
}
