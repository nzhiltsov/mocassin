package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArxivServiceAsync {

	void handle(String arxivId, AsyncCallback<Void> callback);

}
