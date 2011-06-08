package ru.ksu.niimm.cll.mocassin.analyzer.relation.impl;

import ru.ksu.niimm.cll.mocassin.analyzer.relation.ExemplifiesRelationAnalyzer;
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

public class ExemplifiesRelationAnalyzerImpl extends AbstractRelationAnalyzer
		implements ExemplifiesRelationAnalyzer {
	@Inject
	private ExemplifiesRelationAnalyzerImpl(
			StructuralElementSearcher structuralElementSearcher) {
		super(structuralElementSearcher);
	}

	@Override
	public void addRelations(Graph<StructuralElement, Reference> graph,
			ParsedDocument document) {
		this.graph = graph;

		Iterable<StructuralElement> examples = Iterables.filter(graph
				.getVertices(),
				new TypePredicate(MocassinOntologyClasses.EXAMPLE));
		MocassinOntologyClasses[] validDomains = MocassinOntologyRelations
				.getValidRanges(MocassinOntologyRelations.EXEMPLIFIES);
		int refId = -3000; // TODO: more accurate id generation is required; see
		// also GateReferenceSearcherImpl and other restricted relation
		// analyzers
		for (StructuralElement example : examples) {
			StructuralElement closestPredecessor = this.structuralElementSearcher
					.findClosestPredecessor(example, validDomains, graph);
			Reference reference = new ReferenceImpl.Builder(refId--).document(
					document).build();
			reference.setPredictedRelation(MocassinOntologyRelations.EXEMPLIFIES);
			addEdge(reference, example, closestPredecessor);
		}

	}

}
