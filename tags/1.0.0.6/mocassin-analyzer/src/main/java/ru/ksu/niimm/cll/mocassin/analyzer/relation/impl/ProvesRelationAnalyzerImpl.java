package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.TypePredicate;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public class ProvesRelationAnalyzerImpl extends AbstractRelationAnalyzer
		implements ProvesRelationAnalyzer {
	@InjectLogger
	private Logger logger;

	@Inject
	public ProvesRelationAnalyzerImpl(
			StructuralElementSearcher structuralElementSearcher) {
		super(structuralElementSearcher);
	}

	@Override
	public void addRelations(Graph<StructuralElement, Reference> graph,
			ParsedDocument document) {
		this.graph = graph;

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
						.document(document).build();
				reference
						.setPredictedRelation(MocassinOntologyRelations.PROVES);
				addEdge(reference, proof, closestPredecessor);
			} else {
				logger.warn(
						"Failed to add a relation for a proof={}, because the found closest predecessor is null",
						proof.getUri());
			}
		}

	}

}
