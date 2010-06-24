package ru.ksu.niimm.cll.mocassin.parser.mapping;

import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.ose.ontology.OntologyConcept;

public class MappingElement {
	private Node node;
	private OntologyConcept concept;
	/**
	 * confidence value [0..1]
	 */
	private float confidence;

	public MappingElement(Node node, OntologyConcept concept, float confidence) {
		this(node, concept);
		this.confidence = confidence;
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

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
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
		return String.format("%s <-> %s/%s [%f]", node, concept.getUri(),
				concept.getLabel(), confidence);
	}

}
