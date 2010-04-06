package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public class RDFTripleImpl implements RDFTriple {
	private RDFNode subject;
	private RDFNode predicate;
	private RDFNode object;

	public RDFTripleImpl(RDFNode subject, RDFNode predicate, RDFNode object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public RDFNode getSubject() {
		return subject;
	}

	public void setSubject(RDFNode subject) {
		this.subject = subject;
	}

	public RDFNode getPredicate() {
		return predicate;
	}

	public void setPredicate(RDFNode predicate) {
		this.predicate = predicate;
	}

	public RDFNode getObject() {
		return object;
	}

	public void setObject(RDFNode object) {
		this.object = object;
	}

}
