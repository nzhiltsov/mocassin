package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Dashboard implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("1024", "768");
		rootPanel.add(new DashboardTabPanel());

	}

}
