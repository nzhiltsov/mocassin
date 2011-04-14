package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexSearcherParseException;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexStructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ArxivServiceImpl implements ArxivService {
	@Inject
	private Logger logger;
	@Inject
	private ArxivDAOFacade arxivDAOFacade;
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;
	@Inject
	private LatexStructuralElementSearcher latexStructuralElementSearcher;
	@Inject
	private ReferenceTripleUtil referenceTripleUtil;

	@Override
	public void handle(String arxivId) {

		try {
			ArticleMetadata metadata = arxivDAOFacade.retrieve(arxivId);
			InputStream sourceStream = arxivDAOFacade.loadSource(metadata);
			ParsedDocumentImpl document = new ParsedDocumentImpl(metadata
					.getId());
			latexStructuralElementSearcher.parse(sourceStream, document);
			List<Reference> references = latexStructuralElementSearcher.retrieveReferences(document);
			Set<RDFTriple> triples = referenceTripleUtil.convert(references);
			ontologyResourceFacade.insert(metadata, triples);
		} catch (LatexSearcherParseException e) {
			String message = String.format(
					"failed to parse the source of the article %s due to: %s",
					arxivId, e.getMessage());
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}

	}

	@Override
	public List<ArxivArticleMetadata> loadArticles() {
		List<ArticleMetadata> publications = ontologyResourceFacade.loadAll();
		List<ArxivArticleMetadata> articlesList = CollectionUtil
				.asList(Iterables.transform(publications,
						new ArxivArticleMetadataFunction()));
		return articlesList;
	}

	private static class ArxivArticleMetadataFunction implements
			Function<ArticleMetadata, ArxivArticleMetadata> {

		@Override
		public ArxivArticleMetadata apply(ArticleMetadata metadata) {
			return new ArxivArticleMetadata(metadata.getId(), metadata
					.getTitle());
		}

	}
}