package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ViewerServiceAsync {
	void load(String uri, AsyncCallback<ArticleInfo> callback);
}
