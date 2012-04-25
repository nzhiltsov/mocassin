package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.ArXMLivAdapter;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadInfo;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;

import com.google.inject.Inject;

public class ArXMLivAdapterService implements ArxivService {
	@Inject
	private ArXMLivAdapter arXMLivAdapter;

	@Override
	public void handle(String arxivId) {
		try {
			arXMLivAdapter.handle(arxivId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PagingLoadInfo<ArxivArticleMetadata> loadArticles(
			PagingLoadConfig pagingLoadConfig) {
		List<ArxivArticleMetadata> articles = arXMLivAdapter.loadArticles();
		PagingLoadConfig adjustedPagingLoadConfig = PagingLoadConfig
				.adjustPagingLoadConfig(pagingLoadConfig, articles.size());
		List<ArxivArticleMetadata> filteredArticles = new ArrayList<ArxivArticleMetadata>(articles.subList(
				adjustedPagingLoadConfig.getOffset(),
				adjustedPagingLoadConfig.getOffset()
						+ adjustedPagingLoadConfig.getLimit()));
		PagingLoadInfo<ArxivArticleMetadata> pagingLoadInfo = new PagingLoadInfo<ArxivArticleMetadata>();
		pagingLoadInfo.setPagingLoadConfig(pagingLoadConfig);
		pagingLoadInfo.setData(filteredArticles);
		pagingLoadInfo.setFullCollectionSize(articles.size());
		return pagingLoadInfo;
	}

}
