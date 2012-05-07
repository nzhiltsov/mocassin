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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementImpl.TypePredicate;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;
/**
 * This class implements baseline extraction of 'exemplifies' relation instances
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class BasicExemplifiesRelationAnalyzer extends AbstractRelationAnalyzer
	implements ExemplifiesRelationAnalyzer {
    @InjectLogger
    private Logger logger;

    @Inject
    private BasicExemplifiesRelationAnalyzer(
	    StructuralElementSearcher structuralElementSearcher) {
	super(structuralElementSearcher);
    }

    @Override
    public void addRelations(Graph<StructuralElement, Reference> graph,
	    String paperUrl) {

	Iterable<StructuralElement> examples = Iterables.filter(graph
		.getVertices(), new TypePredicate(
		MocassinOntologyClasses.EXAMPLE));
	MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
		.getValidRanges(MocassinOntologyRelations.EXEMPLIFIES);
	int refId = -3000; // TODO: more accurate id generation is required; see
	// also GateReferenceSearcherImpl and other restricted relation
	// analyzers
	for (StructuralElement example : examples) {
	    StructuralElement closestPredecessor = this.structuralElementSearcher
		    .findClosestPredecessor(example, validDomains, graph);
	    if (closestPredecessor != null) {
		Reference reference = new ReferenceImpl.Builder(refId--)
			.document(new ParsedDocumentImpl(paperUrl)).build();
		reference
			.setPredictedRelation(MocassinOntologyRelations.EXEMPLIFIES);
		addEdge(graph, reference, example, closestPredecessor);
	    } else {
		logger.warn(
			"Failed to add a relation for an example={}, because the found closest predecessor is null",
			example.getUri());
	    }
	}

    }

}
