package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

public class ReferenceTripleUtilImpl implements ReferenceTripleUtil {
	private static final String RDFS_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	@Override
	public Set<RDFTriple> convert(List<Reference> references) {
		Set<RDFTriple> triples = new HashSet<RDFTriple>();
		for (Reference ref : references) {
			StructuralElement from = ref.getFrom();
			StructuralElement to = ref.getTo();
			triples.add(createTriple("<%s> <%s> <%s>", from.getUri(),
					RDFS_TYPE, MocassinOntologyClasses.getUri(from
							.getPredictedClass())));
			triples.add(createTriple("<%s> <%s> <%s>", to.getUri(), RDFS_TYPE,
					MocassinOntologyClasses.getUri(to.getPredictedClass())));
			triples.add(createTriple("<%s> <%s> <%s>", from.getUri(), ref
					.getPredictedRelation().getUri(), to.getUri()));
		}
		return triples;
	}

	private static RDFTriple createTriple(String pattern, Object... args) {
		return new RDFTripleImpl(String.format(pattern, args));
	}

}
