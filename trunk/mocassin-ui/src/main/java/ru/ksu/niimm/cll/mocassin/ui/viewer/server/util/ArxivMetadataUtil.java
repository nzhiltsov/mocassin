package ru.ksu.niimm.cll.mocassin.ui.viewer.server.util;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

public class ArxivMetadataUtil {
	private ArxivMetadataUtil() {
	}

	public static List<RDFTriple> convertToTriples(ArticleMetadata metadata) {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		triples.add(convertTitle(metadata));
		for (Author author : metadata.getAuthors()) {
			triples.add(new RDFTripleImpl(String.format(
					"<%s> <http://purl.org/dc/elements/1.1/creator> \"%s\" .",
					metadata.getId(), author.getName())));
		}
		// TODO
		throw new UnsupportedOperationException("not implemented yet");
	}

	private static RDFTriple convertTitle(ArticleMetadata metadata) {
		RDFTriple titleTriple = new RDFTripleImpl(String.format(
				"<%s> <http://purl.org/dc/elements/1.1/title> \"%s\" .",
				metadata.getId(), metadata.getTitle()));
		return titleTriple;
	}
}
