package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.openrdf.model.Statement;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public class ArxivAdapter extends AbstractArXMLivAdapter implements
		ArXMLivAdapter {
	@InjectLogger
	private Logger logger;
	@Inject
	private ArxivDAOFacade arxivDAOFacade;

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
			metadata.setCollectionId(arxivId);
			// Step 2
			InputStream latexSourceStream = arxivDAOFacade.loadSource(metadata);
			latexDocumentDAO.save(arxivId, latexSourceStream, "utf8");
			// Step 3 & partial Step 7
			latexDocumentHeaderPatcher.patch(arxivId);
			pdflatexWrapper.compilePatched(arxivId);
			latex2pdfMapper.generateSummary(arxivId);
			// Step 4
			String arxmlivFilePath = arxmlivProducer.produce(arxivId);
			// Step 5
			gateDocumentDAO.save(arxivId, new File(arxmlivFilePath), "utf8");
			gateProcessingFacade.process(arxivId);
			// Step 6
			Graph<StructuralElement, Reference> graph = extractStructuralElements(metadata);
			// Step 7
			generateHighlightedPdfs(arxivId, graph.getVertices());
			// Step 8
			List<Statement> triples = referenceStatementGenerator
					.convert(graph);
			ontologyResourceFacade.insert(triples); // TODO: Arxiv article
													// metadata must be inserted
													// into a store beforehand

		} catch (Exception e) {
			logger.error("Failed to handle a document with a key = {}",
					arxivId, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Logger getLogger() {
		return this.logger;
	}
}
