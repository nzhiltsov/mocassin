package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfHighlighter;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexWrapper;
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
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private GateProcessingFacade gateProcessingFacade;
	@Inject
	private PdflatexWrapper pdflatexWrapper;
	@Inject
	Latex2PDFMapper latex2pdfMapper;
	@Inject
	PdfHighlighter pdfHighlighter;

	@Override
	public int handle(Set<String> arxivIds) {
		int numberOfSuccesses = 0;
		for (String arxivId : arxivIds) {
			try {
				long start = System.currentTimeMillis();
				handle(arxivId);
				long stop = System.currentTimeMillis();
				logger.log(
						Level.INFO,
						String.format(
								"The document='%s' has been processed in %.2f second(s)",
								arxivId, ((float) (stop - start)) / 1000));
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
		ArticleMetadata metadata;
		try {
			metadata = arxivDAOFacade.retrieve(arxivId);
			metadata.setArxivId(arxivId);
			// Step 2
			InputStream latexSourceStream = arxivDAOFacade.loadSource(metadata);
			latexDocumentDAO.save(arxivId, latexSourceStream);
			// Step 3 & partial Step 7
			latexDocumentHeaderPatcher.patch(arxivId);
			pdflatexWrapper.compilePatched(arxivId);
			latex2pdfMapper.generateSummary(arxivId);
			// Step 4
			String arxmlivFilePath = arxmlivProducer.produce(arxivId);
			// Step 5
			gateDocumentDAO.save(arxivId, new File(arxmlivFilePath));
			gateProcessingFacade.process(arxivId);
			// Step 6
			Graph<StructuralElement, Reference> graph = extractStructuralElements(metadata);
			// Step 7
			Collection<StructuralElement> structuralElements = graph
					.getVertices();
			for (StructuralElement element : structuralElements) {
				int latexStartLine = element.getLatexStartLine();
				int latexEndLine = element.getLatexEndLine();
				if (latexStartLine != 0 && latexEndLine != 0) {
					try {
						pdfHighlighter.generateHighlightedPdf(arxivId,
								element.getId(), latexStartLine, latexEndLine);
					} catch (Exception e) {
						logger.log(
								Level.SEVERE,
								String.format(
										"failed to generate the highlighted PDF for a segment with id='%d' in the document='%s'",
										element.getId(), arxivId));
					}
				}
			}
			// Step 8
			Set<RDFTriple> triples = referenceTripleUtil.convert(graph);
			ontologyResourceFacade.insert(metadata, triples);

		} catch (Exception e) {
			String message = String.format(
					"failed to handle document with id='%s' due to: %s",
					arxivId, e.getMessage());
			logger.log(Level.SEVERE, message);
			throw new RuntimeException(message);
		}
	}

	private Graph<StructuralElement, Reference> extractStructuralElements(
			ArticleMetadata metadata) {
		ParsedDocumentImpl document = getParsedDocument(metadata);
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
		return graph;
	}

	private ParsedDocumentImpl getParsedDocument(ArticleMetadata metadata) {
		Link pdfLink = Iterables.find(metadata.getLinks(),
				new PdfLinkPredicate());
		ParsedDocumentImpl document = new ParsedDocumentImpl(
				metadata.getArxivId(), metadata.getId(), pdfLink.getHref());
		return document;
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
