package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

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
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link.PdfLinkPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
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
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractArXMLivAdapter implements ArXMLivAdapter {
	@Inject
	protected Logger logger;
	@Inject
	protected OntologyResourceFacade ontologyResourceFacade;
	@Inject
	protected ReferenceSearcher referenceSearcher;
	@Inject
	protected ReferenceTripleUtil referenceTripleUtil;
	@Inject
	protected NavigationalRelationClassifier navigationalRelationClassifier;
	@Inject
	protected ProvesRelationAnalyzer provesRelationAnalyzer;
	@Inject
	protected HasConsequenceRelationAnalyzer hasConsequenceRelationAnalyzer;
	@Inject
	protected ExemplifiesRelationAnalyzer exemplifiesRelationAnalyzer;
	@Inject
	protected LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;
	@Inject
	protected LatexDocumentDAO latexDocumentDAO;
	@Inject
	protected ArxmlivProducer arxmlivProducer;
	@Inject
	protected GateDocumentDAO gateDocumentDAO;
	@Inject
	protected GateProcessingFacade gateProcessingFacade;
	@Inject
	protected PdflatexWrapper pdflatexWrapper;
	@Inject
	protected Latex2PDFMapper latex2pdfMapper;
	@Inject
	protected PdfHighlighter pdfHighlighter;

	public AbstractArXMLivAdapter() {
	}

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

	protected void generateHighlightedPdfs(String arxivId,
			Collection<StructuralElement> structuralElements) {
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
	}

	protected Graph<StructuralElement, Reference> extractStructuralElements(
			ArticleMetadata metadata) {
		ParsedDocument document = getParsedDocument(metadata);
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

	private ParsedDocument getParsedDocument(ArticleMetadata metadata) {
		Link pdfLink = Iterables.find(metadata.getLinks(),
				new PdfLinkPredicate());
		ParsedDocument document = new ParsedDocumentImpl(
				metadata.getCollectionId(), metadata.getId(), pdfLink.getHref());
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