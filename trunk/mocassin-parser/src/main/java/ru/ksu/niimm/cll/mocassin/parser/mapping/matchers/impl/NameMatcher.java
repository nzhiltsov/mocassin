package ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.mapping.Mapping;
import ru.ksu.niimm.cll.mocassin.parser.mapping.MappingElement;
import ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.parser.similarity.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.parser.similarity.StringSimilarityEvaluator.SimilarityMetrics;
import ru.ksu.niimm.ose.ontology.OMDocOntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;

public class NameMatcher implements Matcher {
	@Inject
	private StringSimilarityEvaluator stringSimilarityEvaluator;
	@Inject
	private OMDocOntologyFacade omdocOntologyFacade;
	@Inject
	private NameMatcherPropertiesLoader nameMatcherPropertiesLoader;

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

	private void map(List<MappingElement> elements, Node node) {

		for (OntologyConcept concept : getOntologyConcepts()) {
			Map<SimilarityMetrics, Float> confidences = new HashMap<SimilarityMetrics, Float>();
			for (SimilarityMetrics metric : SimilarityMetrics.values()) {
				float similarity = getStringSimilarityEvaluator()
						.getSimilarity(node.getName().toLowerCase(),
								concept.getLabel().toLowerCase(), metric);
				confidences.put(metric, similarity);
			}
			if (!contains(elements, node, concept)) {
				elements.add(new MappingElement(node, concept, confidences));
			}

		}

	}

	private boolean contains(List<MappingElement> elements, Node node,
			OntologyConcept concept) {
		boolean contains = false;
		for (MappingElement element : elements) {
			contains = element.getNode().getName().equals(node.getName())
					&& element.getConcept().equals(concept);
			if (contains) {
				break;
			}
		}
		return contains;
	}

	private StringSimilarityEvaluator getStringSimilarityEvaluator() {
		return stringSimilarityEvaluator;
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
