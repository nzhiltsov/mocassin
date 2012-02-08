package ru.ksu.niimm.cll.mocassin.nlp.util.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.lang.String.format;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;
import edu.uci.ics.jung.graph.Graph;

public class ReferenceTripleUtilImpl implements ReferenceTripleUtil {
	private static final String LITERAL_PATTERN = "<%s> <%s> \"%s\" .";
	private static final String TRIPLE_PATTERN = "<%s> <%s> <%s> .";
	private static final String RDFS_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String INTEGER_PATTERN = "<%s> <%s> \"%d\"^^<http://www.w3.org/2001/XMLSchema#integer> .";
	private static final String SALT_SCHEMA = "http://salt.semanticauthoring.org/ontologies/sdo";
	private static final String SDO_PUBLICATION_TYPE = format("%s#Publication",
			SALT_SCHEMA);

	@Override
	public Set<RDFTriple> convert(Graph<StructuralElement, Reference> graph) {
		Set<RDFTriple> triples = new HashSet<RDFTriple>();
		boolean addedPublication = false;
		for (Reference ref : graph.getEdges()) {
			if (!addedPublication) {
				triples.add(createTriple(ref.getDocument().getUri(), RDFS_TYPE,
						SDO_PUBLICATION_TYPE));
				addedPublication = true;
			}
			
			StructuralElement from = graph.getSource(ref);
			StructuralElement to = graph.getDest(ref);
			
			triples.add(createTypeTriple(from));
			triples.add(createTypeTriple(to));

			if (from.getTitle() != null) {
				triples.add(createTitleTriple(from));
			}
			if (to.getTitle() != null) {
				triples.add(createTitleTriple(to));
			}

			triples.add(createValueTriple(from));
			triples.add(createValueTriple(to));

			triples.add(createPageNumberTriple(from));
			triples.add(createPageNumberTriple(to));

			triples.add(createTriple(from.getUri(), ref.getPredictedRelation()
					.getUri(), to.getUri()));
			triples.add(createTriple(ref.getDocument().getUri(),
					MocassinOntologyRelations.HAS_SEGMENT.getUri(),
					from.getUri()));
			triples.add(createTriple(ref.getDocument().getUri(),
					MocassinOntologyRelations.HAS_SEGMENT.getUri(), to.getUri()));
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

	private static RDFTriple createTitleTriple(StructuralElement element) {
		return new RDFTripleImpl(String.format(LITERAL_PATTERN,
				element.getUri(), MocassinOntologyRelations.HAS_TITLE.getUri(),
				element.getTitle()));
	}

	private static RDFTriple createPageNumberTriple(StructuralElement element) {
		return new RDFTripleImpl(String.format(INTEGER_PATTERN,
				element.getUri(),
				MocassinOntologyRelations.HAS_START_PAGE_NUMBER.getUri(),
				element.getStartPageNumber()));
	}

	private static RDFTriple createValueTriple(StructuralElement element) {
		List<String> contents = element.getContents();
		StringBuffer sb = new StringBuffer();
		for (String str : contents) {
			sb.append(str);
			sb.append(" ");
		}
		String textContents = sb.toString().replace("\n", "");
		return new RDFTripleImpl(String.format(LITERAL_PATTERN,
				element.getUri(), MocassinOntologyRelations.HAS_TEXT.getUri(),
				textContents));
	}
}
