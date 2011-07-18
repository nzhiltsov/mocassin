package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.analyzer.classifier.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.analyzer.classifier.Prediction;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link.PdfLinkPredicate;
import ru.ksu.niimm.cll.mocassin.fulltext.PDFIndexer;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.parser.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.parser.util.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public class ArXMLivAdapterImpl implements ArXMLivAdapter {
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
	private NavigationalRelationClassifier navigationalRelationClassifier;
	@Inject
	private ProvesRelationAnalyzer provesRelationAnalyzer;
	@Inject
	private HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;
	@Inject
	private ExemplifiesRelationAnalyzer exemplifiesRelationAnalyzer;
	@Inject
	private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
	@Inject
	private LatexDocumentDAO latexDocumentDAO;
	@Inject
	private ArxmlivProducer arxmlivProducer;

	@Override
	public int handle(Set<String> arxivIds) {
		int numberOfSuccesses = 0;
		for (String arxivId : arxivIds) {
			try {
				handle(arxivId);
				numberOfSuccesses++;
			} catch (Exception e) {// do nothing
			}
		}
		return numberOfSuccesses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.cll.mocassin.ui.dashboard.server.ArXMLivAdapter#handle(java
	 * .lang.String)
	 */
	@Override
	public void handle(String arxivId) {
		if (arxivId == null || arxivId.length() == 0)
			throw new RuntimeException("arXiv id cannot be null or empty");
		/*
		 * TODO: refactor this method to decouple this class from a bunch of
		 * classes; see Issue 70 for numbering
		 */
		// Step 1
		ArticleMetadata metadata = arxivDAOFacade.retrieve(arxivId);
		metadata.setArxivId(arxivId);
		// Step 2
		InputStream latexSourceStream = arxivDAOFacade.loadSource(metadata);
		latexDocumentDAO.save(arxivId, latexSourceStream);
		// Step 3
		latexDocumentHeaderPatcher.patch(arxivId);
		// Step 4
		arxmlivProducer.produce(arxivId);

		Link pdfLink = Iterables.find(metadata.getLinks(),
				new PdfLinkPredicate());
		ParsedDocumentImpl document = new ParsedDocumentImpl(arxivId,
				metadata.getId(), pdfLink.getHref());
		Graph<StructuralElement, Reference> graph = referenceSearcher
				.retrieveStructuralGraph(document);
		Collection<Reference> edges = graph.getEdges();
		for (Reference reference : edges) {
			if (reference.getPredictedRelation() == null) {
				Prediction prediction = navigationalRelationClassifier.predict(
						reference, graph);
				if (prediction == null)
					continue;
				reference.setPredictedRelation(prediction.getRelation());
			}
		}
		exemplifiesRelationAnalyzer.addRelations(graph, document);
		provesRelationAnalyzer.addRelations(graph, document);
		hasConsequenceRelationAnalyzer.addRelations(graph, document);
		Set<RDFTriple> triples = referenceTripleUtil.convert(graph);
		ontologyResourceFacade.insert(metadata, triples);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.cll.mocassin.ui.dashboard.server.ArXMLivAdapter#loadArticles
	 * ()
	 */
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
