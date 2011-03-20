package ru.ksu.niimm.cll.mocassin.ui.viewer.server;

import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ArxivService;

import com.google.inject.Inject;

public class ArxivServiceImpl implements ArxivService {

	@Inject
	private ArxivDAOFacade arxivDAOFacade;

	@Override
	public void handle(String arxivId) {
		arxivDAOFacade.retrieve(arxivId);
	}
}
