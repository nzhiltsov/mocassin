package ru.ksu.niimm.cll.mocassin.frontend.client;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QueryServiceAsync {

	void query(OntQueryStatement statement, PagingLoadConfig pagingLoadConfig,
			AsyncCallback<PagingLoadInfo<ResultDescription>> callback);

}
