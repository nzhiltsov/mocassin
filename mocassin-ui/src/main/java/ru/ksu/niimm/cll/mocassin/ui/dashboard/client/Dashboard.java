package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;

public class Dashboard implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("1024", "768");

		TabPanel tabPanel = new TabPanel();
		tabPanel.add(new TabBar(), "Upload");
		tabPanel.add(new TabBar(), "Index");
		tabPanel.add(new TabBar(), "Collection Graph");
		rootPanel.add(tabPanel);

	}

}
