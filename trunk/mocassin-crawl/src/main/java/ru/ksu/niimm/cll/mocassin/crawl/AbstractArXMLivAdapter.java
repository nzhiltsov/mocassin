package ru.ksu.niimm.cll.mocassin.crawl;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.analyzer.ReferenceStatementGenerator;
import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link.PdfLinkPredicate;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfHighlighter;
import ru.ksu.niimm.cll.mocassin.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractArXMLivAdapter implements ArXMLivAdapter {

	@Inject
	protected OntologyResourceFacade ontologyResourceFacade;
	@Inject
	protected ReferenceSearcher referenceSearcher;
	@Inject
	protected ReferenceStatementGenerator referenceStatementGenerator;
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

	protected abstract Logger getLogger();

	public int handle(List<String> arxivIds) {
		int numberOfSuccesses = 0;
		for (String arxivId : arxivIds) {
			try {
				long start = System.currentTimeMillis();
				handle(arxivId);
				long stop = System.currentTimeMillis();
				getLogger()
						.info("The document = '{}' has been processed in {} second(s)",
								arxivId,
								Math.round(((float) (stop - start)) / 1000));
				numberOfSuccesses++;
			} catch (Throwable e) {
				getLogger().error("Failed to handle a document with id = '{}'",
						arxivId, e);
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
					getLogger()
							.error("Failed to generate the highlighted PDF for a segment with id = {}",
									format("%s/%d", arxivId, element.getId()),
									e);
				}
			} else {
				getLogger()
						.warn("Skipped generating the highlighted PDF for a segment with id = {}. Perhaps, the segment location is incorrect.",
								format("%s/%d", arxivId, element.getId()));
			}
		}
	}

	protected Graph<StructuralElement, Reference> extractStructuralElements(
			ArticleMetadata metadata) {
		ParsedDocument document = getParsedDocument(metadata);
		Graph<StructuralElement, Reference> graph = referenceSearcher
				.retrieveStructuralGraph(document);
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