package ru.ksu.niimm.cll.mocassin.ui.viewer.server;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;

import com.google.inject.Inject;

public class ArxivServiceImpl implements ArxivService {

	@Inject
	private ArxivDAOFacade arxivDAOFacade;
	@Inject
	private VirtuosoDAO virtuosoDAO;

	@Override
	public void handle(String arxivId) {
		ArticleMetadata metadata = arxivDAOFacade.retrieve(arxivId);
	}
}
