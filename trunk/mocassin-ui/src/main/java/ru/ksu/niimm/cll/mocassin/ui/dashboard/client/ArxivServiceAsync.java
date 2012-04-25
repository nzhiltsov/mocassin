package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArxivServiceAsync {

	void loadArticles(PagingLoadConfig pagingLoadConfig,
			AsyncCallback<PagingLoadInfo<ArxivArticleMetadata>> callback);

}
