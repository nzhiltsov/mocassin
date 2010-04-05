package ru.ksu.niimm.cll.mocassin.virtuoso.generator.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;

public class InsertQueryGeneratorImpl implements InsertQueryGenerator {

	@Override
	public String generate(List<RDFTriple> triples, RDFGraph graph) {
		// TODO: complete implementation, @see QueryManagerFacadeImpl
		throw new UnsupportedOperationException("not implemented yet");
		/*
		 * for (RDFTriple triple : triples) {
		 * 
		 * } return String.format("INSERT INTO GRAPH <%s> { <%s> <%s> <%s>}",
		 * graph .getIri(), triple.getSubjectUri(), triple.getPredicateUri(),
		 * triple.getObjectUri());
		 */
	}
}
