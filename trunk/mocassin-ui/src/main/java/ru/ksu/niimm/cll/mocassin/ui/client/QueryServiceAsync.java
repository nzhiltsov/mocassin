package ru.ksu.niimm.cll.mocassin.ui.client;

import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QueryServiceAsync {

	void query(OntQueryStatement statement, PagingLoadConfig pagingLoadConfig,
			AsyncCallback<PagingLoadInfo<ResultDescription>> callback);

}
