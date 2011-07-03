package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;

import com.google.inject.Inject;

public class ArXMLivAdapterService implements ArxivService {
	@Inject
	private ArXMLivAdapter arXMLivAdapter;

	@Override
	public void handle(String arxivId) {
		arXMLivAdapter.handle(arxivId);
	}

	@Override
	public List<ArxivArticleMetadata> loadArticles() {
		return arXMLivAdapter.loadArticles();
	}

}
