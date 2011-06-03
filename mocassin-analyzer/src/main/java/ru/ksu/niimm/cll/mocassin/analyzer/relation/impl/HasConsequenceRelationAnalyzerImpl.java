package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import java.util.Collection;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl.TypePredicate;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public class HasConsequenceRelationAnalyzerImpl implements
		HasConsequenceRelationAnalyzer {
	@Inject
	private Logger logger;
	private final StructuralElementSearcher structuralElementSearcher;

	private Graph<StructuralElement, Reference> graph;

	@Inject
	public HasConsequenceRelationAnalyzerImpl(
			StructuralElementSearcher structuralElementSearcher) {
		this.structuralElementSearcher = structuralElementSearcher;
	}

	@Override
	public synchronized void addRelations(
			Graph<StructuralElement, Reference> graph, ParsedDocument document) {
		this.graph = graph;

		Iterable<StructuralElement> corollaries = Iterables.filter(graph
				.getVertices(), new TypePredicate(
				MocassinOntologyClasses.COROLLARY));
		MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
				.getValidDomains(MocassinOntologyRelations.HAS_CONSEQUENCE);
		int refId = -1000; // TODO: more accurate id generation is required; see
							// also GateReferenceSearcherImpl
		for (StructuralElement corollary : corollaries) {
			StructuralElement closestPredecessor = this.structuralElementSearcher
					.findClosestPredecessor(corollary, validDomains, graph);
			Reference reference = new ReferenceImpl.Builder(refId--).document(
					document).build();
			reference.setPredictedRelation(MocassinOntologyRelations.HAS_CONSEQUENCE);
			addEdge(reference, corollary, closestPredecessor);
		}

	}

	private void addEdge(Reference edge, final StructuralElement from,
			final StructuralElement to) {
		StructuralElement foundFrom = null;
		StructuralElement foundTo = null;
		if (this.graph.containsVertex(from)) {
			foundFrom = findVertice(from);
		}
		if (this.graph.containsVertex(to)) {
			foundTo = findVertice(to);
		}
		this.graph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	private StructuralElement findVertice(StructuralElement node) {
		Collection<StructuralElement> vertices = this.graph.getVertices();
		for (StructuralElement cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}
}
