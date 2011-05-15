package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.analyzer.classifier.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.analyzer.classifier.Prediction;
import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.arxiv.LoadingPdfException;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link.PdfLinkPredicate;
import ru.ksu.niimm.cll.mocassin.fulltext.PDFIndexer;
import ru.ksu.niimm.cll.mocassin.fulltext.PersistingDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public class ArXMLivAdapterService implements ArxivService {
	@Inject
	private Logger logger;
	@Inject
	private ArxivDAOFacade arxivDAOFacade;
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private ReferenceTripleUtil referenceTripleUtil;
	@Inject
	private PDFIndexer pdfIndexer;
	@Inject
	private NavigationalRelationClassifier navigationalRelationClassifier;

	@Override
	public void handle(String arxivId) {
		try {
			ArticleMetadata metadata = arxivDAOFacade.retrieve(arxivId);
			InputStream pdfInputStream = arxivDAOFacade.loadPDF(metadata);
			Link pdfLink = Iterables.find(metadata.getLinks(),
					new PdfLinkPredicate());
			pdfIndexer.save(pdfLink.getHref(), pdfInputStream);
			ParsedDocumentImpl document = new ParsedDocumentImpl(arxivId, metadata
					.getId(), pdfLink.getHref());
			Graph<StructuralElement, Reference> graph = referenceSearcher
					.retrieveReferences(document);
			Collection<Reference> edges = graph.getEdges();
			for (Reference reference : edges) {
				if (reference.getPredictedRelation() == null) {
					Prediction prediction = navigationalRelationClassifier
							.predict(reference, graph);
					if (prediction == null)
						continue;
					reference.setPredictedRelation(prediction.getRelation());
				}
			}
			Set<RDFTriple> triples = referenceTripleUtil.convert(graph);
			ontologyResourceFacade.insert(metadata, triples);
		} catch (PersistingDocumentException e) {
			String message = String
					.format(
							"failed to persist the PDF index of the article %s due to: %s",
							arxivId, e.getMessage());
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		} catch (LoadingPdfException e) {
			String message = String.format(
					"failed to load PDF of the article %s due to: %s", arxivId,
					e.getMessage());
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
			return new ArxivArticleMetadata(metadata.getId(),
					metadata.getTitle());
		}

	}
}
