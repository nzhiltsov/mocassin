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

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
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

public class HasConsequenceRelationAnalyzerImpl extends
		AbstractRelationAnalyzer implements HasConsequenceRelationAnalyzer {
	@InjectLogger
	private Logger logger;

	@Inject
	protected HasConsequenceRelationAnalyzerImpl(
			StructuralElementSearcher structuralElementSearcher) {
		super(structuralElementSearcher);
	}

	@Override
	public void addRelations(Graph<StructuralElement, Reference> graph,
			ParsedDocument document) {

		Iterable<StructuralElement> corollaries = Iterables.filter(graph
				.getVertices(), new TypePredicate(
				MocassinOntologyClasses.COROLLARY));
		MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
				.getValidDomains(MocassinOntologyRelations.HAS_CONSEQUENCE);
		int refId = -1000; // TODO: more accurate id generation is required; see
		// also GateReferenceSearcherImpl and other restricted relation
		// analyzers
		for (StructuralElement corollary : corollaries) {
			StructuralElement closestPredecessor = this.structuralElementSearcher
					.findClosestPredecessor(corollary, validDomains, graph);
			if (closestPredecessor != null) {
				Reference reference = new ReferenceImpl.Builder(refId--)
						.document(document).build();
				reference
						.setPredictedRelation(MocassinOntologyRelations.HAS_CONSEQUENCE);
				addEdge(graph, reference, closestPredecessor, corollary);
			} else {
				logger.warn(
						"Failed to add a relation for a corollary={}, because the found closest predecessor is null",
						corollary.getUri());
			}
		}

	}

}
