package ru.ksu.niimm.cll.mocassin.virtuoso;

public interface RDFTriple {
	RDFNode getSubject();

	RDFNode getPredicate();

	RDFNode getObject();
}
