package ru.ksu.niimm.cll.mocassin.analyzer.impl;

import static java.lang.String.format;
import static ru.ksu.niimm.cll.mocassin.ontology.model.URIConstants.createIntegerTriple;
import static ru.ksu.niimm.cll.mocassin.ontology.model.URIConstants.createLiteralTriple;
import static ru.ksu.niimm.cll.mocassin.ontology.model.URIConstants.createRdfTypeTriple;
import static ru.ksu.niimm.cll.mocassin.ontology.model.URIConstants.createTriple;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.analyzer.ReferenceStatementGenerator;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import edu.uci.ics.jung.graph.Graph;

public class ReferenceStatementGeneratorImpl implements
		ReferenceStatementGenerator {
	private static final String EMPTY_STRING = "";
	private static final String END_LINE = "\n";
	private static final String SPACE = " ";
	private static final String SALT_SCHEMA = "http://salt.semanticauthoring.org/ontologies/sdo";
	private static final String SDO_PUBLICATION_TYPE = format("%s#Publication",
			SALT_SCHEMA);

	@Override
	public List<Statement> convert(Graph<StructuralElement, Reference> graph) {
		List<Statement> triples = new ArrayList<Statement>();
		boolean addedPublication = false;
		for (Reference ref : graph.getEdges()) {
			if (!addedPublication) {
				triples.add(createRdfTypeTriple(ref.getDocument().getUri(),
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

			Statement fromValueTriple = createValueTriple(from);
			if (fromValueTriple != null) {
				triples.add(fromValueTriple);
			}
			Statement toValueTriple = createValueTriple(to);
			if (toValueTriple != null) {
				triples.add(toValueTriple);
			}
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

	private static Statement createTypeTriple(StructuralElement element) {
		return createRdfTypeTriple(element.getUri(),
				MocassinOntologyClasses.getUri(element.getPredictedClass()));
	}

	private static Statement createTitleTriple(StructuralElement element) {
		return createLiteralTriple(element.getUri(),
				MocassinOntologyRelations.HAS_TITLE.getUri(),
				element.getTitle());
	}

	private static Statement createPageNumberTriple(StructuralElement element) {
		return createIntegerTriple(element.getUri(),
				MocassinOntologyRelations.HAS_START_PAGE_NUMBER.getUri(),
				element.getStartPageNumber());
	}

	private static Statement createValueTriple(StructuralElement element) {
		List<String> contents = element.getContents();
		if (contents == null)
			return null;
		StringBuffer sb = new StringBuffer();
		for (String str : contents) {
			sb.append(str);
			sb.append(SPACE);
		}
		String textContents = sb.toString().replace(END_LINE, EMPTY_STRING);
		return createLiteralTriple(element.getUri(),
				MocassinOntologyRelations.HAS_TEXT.getUri(), textContents);
	}
}
