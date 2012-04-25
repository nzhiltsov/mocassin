package ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping;

import java.util.HashMap;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.Node;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.util.StringSimilarityEvaluator.SimilarityMetrics;

public class MappingElement {
	private Node node;
	private OntologyConcept concept;
	private Map<SimilarityMetrics, Float> confidences = new HashMap<SimilarityMetrics, Float>();

	public MappingElement(Node node, OntologyConcept concept,
			Map<SimilarityMetrics, Float> confidences) {
		this.node = node;
		this.concept = concept;
		this.confidences = confidences;
	}

	public MappingElement(Node node, OntologyConcept concept) {
		this.node = node;
		this.concept = concept;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public OntologyConcept getConcept() {
		return concept;
	}

	public void setConcept(OntologyConcept concept) {
		this.concept = concept;
	}

	public Map<SimilarityMetrics, Float> getConfidences() {
		return confidences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concept == null) ? 0 : concept.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingElement other = (MappingElement) obj;
		if (concept == null) {
			if (other.concept != null)
				return false;
		} else if (!concept.equals(other.concept))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer confidenceValues = new StringBuffer();
		for (SimilarityMetrics metrics : confidences.keySet()) {
			confidenceValues.append(String.format(" %f |", confidences
					.get(metrics)));
		}
		return String.format("%s | %s | %s ", node.getName(), concept.getUri(),
				confidenceValues.toString());
	}
}
