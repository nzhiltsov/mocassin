package ru.ksu.niimm.cll.mocassin.analyzer.classifier;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

public class Prediction {
	private final MocassinOntologyRelations relation;
	private final double confidence;

	public Prediction(MocassinOntologyRelations relation, double confidence) {
		this.relation = relation;
		this.confidence = confidence;
	}

	public MocassinOntologyRelations getRelation() {
		return relation;
	}

	public double getConfidence() {
		return confidence;
	}

}
