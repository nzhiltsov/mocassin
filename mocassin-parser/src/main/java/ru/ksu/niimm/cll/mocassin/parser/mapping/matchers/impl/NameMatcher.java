package ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private static final double SIMILARITY_THRESHOLD = 0;
	@Inject
	private StringSimilarityEvaluator stringSimilarityEvaluator;
	@Inject
	private OMDocOntologyFacade omdocOntologyFacade;

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
		float maxSimilarity = 0;
		MappingElement closestElement = null;
		for (OntologyConcept concept : getOntologyConcepts()) {
			
			if (!containsNode(elements, node)) {
				float similarity = getStringSimilarityEvaluator()
						.getSimilarity(node.getName().toLowerCase(),
								concept.getLabel().toLowerCase(),
								SimilarityMetrics.N_GRAM);
				if (similarity > maxSimilarity) {
					maxSimilarity = similarity;
					closestElement = new MappingElement(node, concept);
					closestElement.setConfidence(similarity);
				}
			}
		}

		if (closestElement != null && maxSimilarity >= SIMILARITY_THRESHOLD) {
			elements.add(closestElement);
		}
	}

	private boolean containsNode(List<MappingElement> elements, Node node) {
		boolean found = false;
		for (MappingElement element : elements) {
			if (element.getNode().equals(node)) {
				found = true;
				break;
			}
		}
		return found;
	}

	private StringSimilarityEvaluator getStringSimilarityEvaluator() {
		return stringSimilarityEvaluator;
	}

	private OMDocOntologyFacade getOmdocOntologyFacade() {
		return omdocOntologyFacade;
	}

	public List<OntologyConcept> getOntologyConcepts() {
		return getOmdocOntologyFacade().getOntClassList();
	}

}
