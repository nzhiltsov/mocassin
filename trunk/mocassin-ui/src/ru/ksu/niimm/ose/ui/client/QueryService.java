package ru.ksu.niimm.ose.ui.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GWT.rpc")
public interface QueryService extends RemoteService {
	public PagingLoadInfo<ResultDescription> query(OntQueryStatement statement, PagingLoadConfig pagingLoadConfig);
}
