package ru.ksu.niimm.cll.mocassin.virtuoso;

public interface RDFNode {
	enum Type {
		IRI_REFERENCE, RDF_LITERAL, NUMERIC_LITERAL, BOOLEAN_LITERAL, BLANK_NODE
	}

	String getUri();

	Type getType();
}
