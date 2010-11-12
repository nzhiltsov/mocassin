package ru.ksu.niimm.cll.mocassin.analyzer.relation;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;


public class RelationInfo {
	private MocassinOntologyRelations relation;
	private String filename;
	private int domainId;
	private int rangeId;

	public MocassinOntologyRelations getRelation() {
		return relation;
	}

	public void setRelation(MocassinOntologyRelations relation) {
		this.relation = relation;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public int getRangeId() {
		return rangeId;
	}

	public void setRangeId(int rangeId) {
		this.rangeId = rangeId;
	}

}
