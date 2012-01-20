package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import edu.uci.ics.jung.graph.Graph;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public class MathnetAdapter extends AbstractArXMLivAdapter implements
		ArXMLivAdapter {
	/**
	 * TODO: move to a configuration file
	 */
	private static final String MATHNET_DOWNLOAD_PDF_URL = "http://www.mathnet.ru/php/getFT.phtml?jrnid=%s&paperid=%s&what=fullt&option_lang=rus";

	@Override
	public void handle(String mathnetKey) {
		if (mathnetKey == null || mathnetKey.length() == 0)
			throw new RuntimeException("Mathnet key id cannot be null or empty");
		/*
		 * TODO: refactor this method to decouple this class from a bunch of
		 * classes; see Issue 70 for numbering
		 */

		try {
			ArticleMetadata metadata = new ArticleMetadata();
			metadata.setId("http://mathnet.ru/" + mathnetKey);
			metadata.setCollectionId(mathnetKey);
			ArrayList<Link> links = new ArrayList<Link>();

			links.add(Link.pdfLink(String.format(MATHNET_DOWNLOAD_PDF_URL,
					StringUtil.extractJournalPrefixFromMathnetKey(mathnetKey),
					StringUtil.extractPaperNumberFromMathnetKey(mathnetKey))));
			metadata.setLinks(links);
			// Step 3 & partial Step 7
			latexDocumentHeaderPatcher.patch(mathnetKey);
			pdflatexWrapper.compilePatched(mathnetKey);
			latex2pdfMapper.generateSummary(mathnetKey);
			// Step 4
			String arxmlivFilePath = arxmlivProducer.produce(mathnetKey);
			// Step 5
			gateDocumentDAO.save(mathnetKey, new File(arxmlivFilePath), "utf8");
			gateProcessingFacade.process(mathnetKey);
			// Step 6
			Graph<StructuralElement, Reference> graph = extractStructuralElements(metadata);
			// Step 7
			generateHighlightedPdfs(mathnetKey, graph.getVertices());
			// Step 8
			Set<RDFTriple> triples = referenceTripleUtil.convert(graph);
			ontologyResourceFacade.insert(metadata, triples);

		} catch (Exception e) {
			/*
			 * String message = String.format(
			 * "failed to handle document with id='%s' due to: %s", mathnetKey,
			 * e.getMessage());
			 */
			logger.log(Level.SEVERE, e.toString());
			throw new RuntimeException(e);
		}

	}

}
