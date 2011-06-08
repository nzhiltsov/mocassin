package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArxivServiceAsync {

	void handle(String arxivId, AsyncCallback<Void> callback);

	void loadArticles(AsyncCallback<List<ArxivArticleMetadata>> callback);

}
