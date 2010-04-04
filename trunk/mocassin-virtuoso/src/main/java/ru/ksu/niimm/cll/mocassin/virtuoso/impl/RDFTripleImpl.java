package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public class RDFTripleImpl implements RDFTriple {
	private String subjectUri;
	private String predicateUri;
	private String objectUri;

	public RDFTripleImpl(String subjectUri, String predicateUri,
			String objectUri) {
		this.subjectUri = subjectUri;
		this.predicateUri = predicateUri;
		this.objectUri = objectUri;
	}

	public String getSubjectUri() {
		return subjectUri;
	}

	public void setSubjectUri(String subjectUri) {
		this.subjectUri = subjectUri;
	}

	public String getPredicateUri() {
		return predicateUri;
	}

	public void setPredicateUri(String predicateUri) {
		this.predicateUri = predicateUri;
	}

	public String getObjectUri() {
		return objectUri;
	}

	public void setObjectUri(String objectUri) {
		this.objectUri = objectUri;
	}

}
