package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArxivServiceAsync {

	void handle(String arxivId, AsyncCallback<Void> callback);

	void loadArticles(PagingLoadConfig pagingLoadConfig,
			AsyncCallback<PagingLoadInfo<ArxivArticleMetadata>> callback);

}
