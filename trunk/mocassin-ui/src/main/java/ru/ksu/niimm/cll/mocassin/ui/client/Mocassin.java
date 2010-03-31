package ru.ksu.niimm.cll.mocassin.ui.client;

import ru.ksu.niimm.cll.mocassin.ui.client.CenterPanel.BuildQueryStatementHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mocassin implements EntryPoint {
	interface Binder extends UiBinder<ScrollPanel, Mocassin> {
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
		ScrollPanel outer = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		root.forceLayout();
		topPanel.sendButton.addClickHandler(new BuildQueryStatementHandler(
				topPanel.tree, centerPanel));
	}

}
