package ru.ksu.niimm.cll.mocassin.ui.client;


import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GWT.rpc")
public interface QueryService extends RemoteService {
	public PagingLoadInfo<ResultDescription> query(OntQueryStatement statement, PagingLoadConfig pagingLoadConfig);
}
