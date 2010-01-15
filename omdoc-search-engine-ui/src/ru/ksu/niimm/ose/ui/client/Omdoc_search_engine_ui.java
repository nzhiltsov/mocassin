package ru.ksu.niimm.ose.ui.client;

import ru.ksu.niimm.ose.ui.client.CenterPanel.BuildQueryStatementHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Omdoc_search_engine_ui implements EntryPoint {
	interface Binder extends UiBinder<VerticalPanel, Omdoc_search_engine_ui> {
	}

	private static final Binder binder = GWT.create(Binder.class);


	@UiField
	TopPanel topPanel;
	@UiField
	CenterPanel centerPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		VerticalPanel outer = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		root.forceLayout();
		topPanel.sendButton.addClickHandler(new BuildQueryStatementHandler(topPanel, centerPanel));
	}

}
