package ru.ksu.niimm.cll.mocassin.ui.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QueryServiceAsync {

	void query(OntQueryStatement statement, PagingLoadConfig pagingLoadConfig,
			AsyncCallback<PagingLoadInfo<ResultDescription>> callback);

}
