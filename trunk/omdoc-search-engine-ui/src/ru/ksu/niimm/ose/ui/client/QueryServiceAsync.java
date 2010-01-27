package ru.ksu.niimm.ose.ui.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QueryServiceAsync {

	void query(OntQueryStatement statement,
			AsyncCallback<List<ResultDescription>> callback);

}
