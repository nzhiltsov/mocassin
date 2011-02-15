package ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.MappingElement;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.ose.ontology.OMDocOntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;

public class NameMatcher implements Matcher {
	@Inject
	private OMDocOntologyFacade omdocOntologyFacade;
	@Inject
	private NameMatcherPropertiesLoader nameMatcherPropertiesLoader;
	private static float STRING_SIMILARITY_THRESHOLD = 0.26087f;

	@Override
	public Mapping doMapping(List<Edge<Node, Node>> graph) {
		List<MappingElement> elements = new ArrayList<MappingElement>();
		for (Edge<Node, Node> edge : graph) {
			Node from = edge.getFrom();
			Node to = edge.getTo();
			map(elements, from);
			map(elements, to);
		}
		return new Mapping(elements);
	}

	private void map(List<MappingElement> elements, final Node node) {
		if (!contains(elements, node)) {

			Function<OntologyConcept, MappingElement> stringSimilarityFunction = new Function<OntologyConcept, MappingElement>() {

				@Override
				public MappingElement apply(OntologyConcept concept) {
					Map<SimilarityMetrics, Float> confidences = computeConfidence(
							node, concept);
					boolean isSimilar = confidences
							.get(SimilarityMetrics.N_GRAM) >= STRING_SIMILARITY_THRESHOLD;
					return isSimilar ? new MappingElement(node, concept,
							confidences) : null;
				}
			};

			Iterable<MappingElement> similarConcepts = Iterables.transform(
					getOntologyConcepts(), stringSimilarityFunction);

			Comparator<MappingElement> bySmithWaterman = new Comparator<MappingElement>() {

				@Override
				public int compare(MappingElement firstElement,
						MappingElement secondElement) {
					Float firstSim = firstElement.getConfidences().get(
							SimilarityMetrics.N_GRAM);
					Float secondSim = secondElement.getConfidences().get(
							SimilarityMetrics.N_GRAM);
					if (firstSim > secondSim) {
						return 1;
					}
					if (firstSim < secondSim) {
						return -1;
					}
					return 0;
				}
			};

			MappingElement mostSimilarElement = Ordering.from(bySmithWaterman)
					.nullsFirst().max(similarConcepts);
			if (mostSimilarElement != null) {
				elements.add(mostSimilarElement);
			}

		}
	}

	/**
	 * compute the mapping elements' confidence according to string similarity
	 * measures
	 * 
	 * @param node
	 * @param concept
	 * @return
	 */
	private Map<SimilarityMetrics, Float> computeConfidence(Node node,
			OntologyConcept concept) {
		Map<SimilarityMetrics, Float> confidences = new HashMap<SimilarityMetrics, Float>();
		float similarity = StringSimilarityEvaluator.getSimilarity(
				node.getName(), concept.getLabel(),
				SimilarityMetrics.N_GRAM);
		confidences.put(SimilarityMetrics.N_GRAM, similarity);
		return confidences;
	}

	private boolean contains(List<MappingElement> elements, Node node) {
		boolean contains = false;
		for (MappingElement element : elements) {
			contains = element.getNode().getName().equals(node.getName());
			if (contains) {
				break;
			}
		}
		return contains;
	}

	private OMDocOntologyFacade getOmdocOntologyFacade() {
		return omdocOntologyFacade;
	}

	public List<String> getMatchedURIs() {
		return nameMatcherPropertiesLoader.getMatchedURIs();
	}

	public Iterable<OntologyConcept> getOntologyConcepts() {
		List<OntologyConcept> ontClassList = getOmdocOntologyFacade()
				.getOntClassList();
		Predicate<OntologyConcept> matchUriPredicate = new Predicate<OntologyConcept>() {

			@Override
			public boolean apply(OntologyConcept o) {
				String uri = o.getUri();
				return getMatchedURIs().contains(uri);
			}
		};
		Iterable<OntologyConcept> filteredConcepts = Iterables.filter(
				ontClassList, matchUriPredicate);
		return filteredConcepts;
	}
}
