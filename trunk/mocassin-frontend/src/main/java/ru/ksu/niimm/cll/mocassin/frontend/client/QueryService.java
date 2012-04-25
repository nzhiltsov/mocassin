package ru.ksu.niimm.cll.mocassin.frontend.client;


import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GWT.rpc")
public interface QueryService extends RemoteService {
	public PagingLoadInfo<ResultDescription> query(OntQueryStatement statement, PagingLoadConfig pagingLoadConfig);
}
