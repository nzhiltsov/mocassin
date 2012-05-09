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

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.StructuralElementImpl.TypePredicate;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;
/**
 * This class implements baseline extraction of 'hasConsequence' relation instances
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class BasicProvesRelationAnalyzer extends AbstractRelationAnalyzer
	implements ProvesRelationAnalyzer {
    @InjectLogger
    private Logger logger;

    @Inject
    private BasicProvesRelationAnalyzer(
	    StructuralElementSearcher structuralElementSearcher) {
	super(structuralElementSearcher);
    }

    @Override
    public void addRelations(Graph<StructuralElement, Reference> graph,
	    String paperUrl) {

	Iterable<StructuralElement> proofs = Iterables.filter(graph
		.getVertices(),
		new TypePredicate(MocassinOntologyClasses.PROOF));
	MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
		.getValidRanges(MocassinOntologyRelations.PROVES);
	int refId = -2000; // TODO: more accurate id generation is required; see
	// also GateReferenceSearcherImpl and other restricted relation
	// analyzers
	for (StructuralElement proof : proofs) {
	    StructuralElement closestPredecessor = this.structuralElementSearcher
		    .findClosestPredecessor(proof, validDomains, graph);
	    if (closestPredecessor != null) {
		Reference reference = new ReferenceImpl.Builder(refId--)
			.document(new ParsedDocumentImpl(paperUrl)).build();
		reference
			.setPredictedRelation(MocassinOntologyRelations.PROVES);
		addEdge(graph, reference, proof, closestPredecessor);
	    } else {
		logger.warn(
			"Failed to add a relation for a proof={}, because the found closest predecessor is null",
			proof.getUri());
	    }
	}

    }

}
