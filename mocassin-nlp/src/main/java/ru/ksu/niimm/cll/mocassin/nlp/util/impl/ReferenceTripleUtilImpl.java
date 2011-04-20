package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

public class ReferenceTripleUtilImpl implements ReferenceTripleUtil {
	private static final String TRIPLE_PATTERN = "<%s> <%s> <%s> .";
	private static final String RDFS_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String INTEGER_PATTERN = "<%s> <%s> \"%d\"^^<http://www.w3.org/2001/XMLSchema#integer>";

	@Override
	public Set<RDFTriple> convert(List<Reference> references) {
		Set<RDFTriple> triples = new HashSet<RDFTriple>();
		for (Reference ref : references) {
			StructuralElement from = ref.getFrom();
			StructuralElement to = ref.getTo();
			triples.add(createTypeTriple(from));
			triples.add(createTypeTriple(to));

			triples.add(createValueTriple(from));
			triples.add(createValueTriple(to));

			triples.add(createPageNumberTriple(from));
			triples.add(createPageNumberTriple(to));
			
			triples.add(createTriple(from.getUri(), ref.getPredictedRelation()
					.getUri(), to.getUri()));
			triples.add(createTriple(ref.getDocument().getFilename(),
					MocassinOntologyRelations.HAS_SEGMENT.getUri(), from
							.getUri()));
			triples
					.add(createTriple(ref.getDocument().getFilename(),
							MocassinOntologyRelations.HAS_SEGMENT.getUri(), to
									.getUri()));
		}
		return triples;
	}

	private static RDFTriple createTriple(Object... args) {
		return new RDFTripleImpl(String.format(TRIPLE_PATTERN, args));
	}

	private static RDFTriple createTypeTriple(StructuralElement element) {
		return createTriple(element.getUri(), RDFS_TYPE,
				MocassinOntologyClasses.getUri(element.getPredictedClass()));
	}

	private static RDFTriple createPageNumberTriple(StructuralElement element) {
		return new RDFTripleImpl(String.format(INTEGER_PATTERN, element
				.getUri(), MocassinOntologyRelations.HAS_START_PAGE_NUMBER
				.getUri(), element.getStartPageNumber()));
	}

	private static RDFTriple createValueTriple(StructuralElement element) {
		return new RDFTripleImpl(String.format("<%s> <%s> \"%s\"", element
				.getUri(), MocassinOntologyRelations.HAS_TEXT.getUri(), element
				.getContents()));
	}
}
