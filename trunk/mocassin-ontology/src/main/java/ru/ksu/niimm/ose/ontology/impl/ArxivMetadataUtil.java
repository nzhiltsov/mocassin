package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

public class ArxivMetadataUtil {
	private static final String ARXIV_IDENTIFIER = "http://arxiv.org/schema#id";// TODO:
																				// the
																				// fake
																				// schema
																				// is
																				// used!

	private static final String ATOM_RDF_SCHEMA = "http://djpowell.net/schemas/atomrdf/0.3";

	private static final String SALT_SCHEMA = "http://salt.semanticauthoring.org/ontologies/sdo";

	private static final String RDFS_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	private ArxivMetadataUtil() {
	}

	public static List<RDFTriple> convertToTriples(ArticleMetadata metadata) {
		List<RDFTriple> triples = new LinkedList<RDFTriple>();
		triples.add(createTriple("<%s> <%s> <%s#Publication> .",
				metadata.getId(), RDFS_TYPE, SALT_SCHEMA));
		convertTitle(triples, metadata);
		convertAuthors(triples, metadata);
		convertLinks(triples, metadata);
		convertKey(triples, metadata);
		return triples;
	}

	private static void convertLinks(List<RDFTriple> triples,
			ArticleMetadata metadata) {
		for (Link link : metadata.getLinks()) {
			String linkNode = StringUtil.generateBlankNodeId();
			triples.add(createTriple("<%s> <%s/link> %s .", metadata.getId(),
					ATOM_RDF_SCHEMA, linkNode));
			triples.add(createTriple("%s <%s> <%s/Link> .", linkNode,
					RDFS_TYPE, ATOM_RDF_SCHEMA));
			triples.add(createTriple("%s <%s/linkType> \"%s\" .", linkNode,
					ATOM_RDF_SCHEMA, link.getType()));
			triples.add(createTriple("%s <%s/linkHref> <%s> .", linkNode,
					ATOM_RDF_SCHEMA, link.getHref()));
		}
	}

	private static void convertAuthors(List<RDFTriple> triples,
			ArticleMetadata metadata) {
		for (Author author : metadata.getAuthors()) {
			String authorNode = StringUtil.generateBlankNodeId();
			triples.add(createTriple("<%s> <%s/author> %s .", metadata.getId(),
					ATOM_RDF_SCHEMA, authorNode));
			triples.add(createTriple("%s <%s> <%s/Person> .", authorNode,
					RDFS_TYPE, ATOM_RDF_SCHEMA));
			triples.add(createTriple("%s <%s/personName> \"%s\" .", authorNode,
					ATOM_RDF_SCHEMA, author.getName()));
		}
	}

	private static RDFTriple createTriple(String pattern, Object... args) {
		return new RDFTripleImpl(String.format(pattern, args));
	}

	private static void convertTitle(List<RDFTriple> triples,
			ArticleMetadata metadata) {
		String titleNode = StringUtil.generateBlankNodeId();
		triples.add(createTriple("<%s> <%s/title> %s .", metadata.getId(),
				ATOM_RDF_SCHEMA, titleNode));
		triples.add(createTriple("%s <%s> <%s/PlainText> .", titleNode,
				RDFS_TYPE, ATOM_RDF_SCHEMA));
		String title = metadata.getTitle() != null ? metadata.getTitle()
				.replaceAll("\n", "") : "";
		triples.add(createTriple("%s <%s/textValue> \"%s\" .", titleNode,
				ATOM_RDF_SCHEMA, title));
	}

	private static void convertKey(List<RDFTriple> triples,
			ArticleMetadata metadata) {
		triples.add(createTriple("<%s> <%s> \"%s\" .", metadata.getId(),
				ARXIV_IDENTIFIER, metadata.getArxivId()));
	}
}
