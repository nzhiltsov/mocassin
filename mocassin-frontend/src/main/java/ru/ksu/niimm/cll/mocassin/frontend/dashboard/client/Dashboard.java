package ru.ksu.niimm.cll.mocassin.frontend.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class Dashboard implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(new DashboardTabPanel());
		root.forceLayout();
	}

}
