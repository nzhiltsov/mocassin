package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GWT.rpc")
public interface ArxivService extends RemoteService {
	public void handle(String arxivId);
}
