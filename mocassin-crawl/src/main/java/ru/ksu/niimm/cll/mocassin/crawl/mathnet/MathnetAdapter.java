package ru.ksu.niimm.cll.mocassin.crawl.mathnet;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.crawl.AbstractArXMLivAdapter;
import ru.ksu.niimm.cll.mocassin.crawl.ArXMLivAdapter;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;
import edu.uci.ics.jung.graph.Graph;

public class MathnetAdapter extends AbstractArXMLivAdapter implements
		ArXMLivAdapter {
	
	private static final String GATE_DOCUMENT_ENCODING = "utf8";
	private static final String ID_PATTERN = "%s%s";
	private static final String MATHNET_PREFIX = "http://mathnet.ru/";
	@InjectLogger
	private Logger logger;
	/**
	 * TODO: move to a configuration file
	 */
	private static final String MATHNET_DOWNLOAD_PDF_URL = "http://www.mathnet.ru/php/getFT.phtml?jrnid=%s&paperid=%s&what=fullt&option_lang=rus";

	@Override
	public void handle(String mathnetKey) throws Exception {
		if (mathnetKey == null || mathnetKey.length() == 0)
			throw new RuntimeException("Mathnet key id cannot be null or empty");
		/*
		 * TODO: refactor this method to decouple this class from a bunch of
		 * classes; see Issue 70 for numbering
		 */

			ArticleMetadata metadata = new ArticleMetadata();
		metadata.setId(format(ID_PATTERN, MATHNET_PREFIX, mathnetKey));
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
			gateDocumentDAO.save(mathnetKey, new File(arxmlivFilePath), GATE_DOCUMENT_ENCODING);
			gateProcessingFacade.process(mathnetKey);
			// Step 6
			Graph<StructuralElement, Reference> graph = extractStructuralElements(metadata);
			// Step 7
			generateHighlightedPdfs(mathnetKey, graph.getVertices());
			// Step 8
			List<Statement> triples = referenceStatementGenerator
					.convert(graph);

			if (!ontologyResourceFacade.insert(triples)) {
				throw new RuntimeException("Failed to insert triples.");
			}
	}

	@Override
	protected Logger getLogger() {
		return this.logger;
	}

}
