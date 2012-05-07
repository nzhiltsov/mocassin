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

import java.util.List;

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

/**
 * The class implements exporting Mocassin's internal representation into
 * Sesame's RDF statements
 * 
 * @author Nikita Zhiltsov
 * 
 */
public interface ReferenceStatementExporter {
    /**
     * Exports a given graph into a list of Sesame's RDF statements
     * 
     * @param graph
     * @returns a list of Sesame's RDF statements
     */
    List<Statement> export(Graph<StructuralElement, Reference> graph);
}
