/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.arxiv;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.openrdf.model.Statement;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.AbstractDomainAdapter;
import ru.ksu.niimm.cll.mocassin.crawl.DomainAdapter;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;

public class ArxivAdapter extends AbstractDomainAdapter implements
		DomainAdapter {
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
	public String handle(String arxivId) throws Exception {
		if (arxivId == null || arxivId.length() == 0)
			throw new RuntimeException("arXiv id cannot be null or empty");
		/*
		 * TODO: refactor this method to decouple this class from a bunch of
		 * classes; see Issue 70 for numbering
		 */
		// Step 1
		ArticleMetadata metadata;
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
		List<Statement> triples = referenceStatementGenerator.convert(graph);
		ontologyResourceFacade.insert(triples); // TODO: Arxiv article
												// metadata must be inserted
												// into a store beforehand
		return null;
	}

	@Override
	protected Logger getLogger() {
		return this.logger;
	}
}
