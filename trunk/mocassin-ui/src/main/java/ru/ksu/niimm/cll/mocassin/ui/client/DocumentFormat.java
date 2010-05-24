package ru.ksu.niimm.cll.mocassin.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DocumentFormat extends Composite {
	interface Binder extends UiBinder<HorizontalPanel, DocumentFormat> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	Anchor link;

	private String uri;

	public DocumentFormat() {
		initWidget(binder.createAndBindUi(this));
	}

	public void setText(String text) {
		link.setText(text);
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@UiHandler("link")
	void handleClick(ClickEvent event) {

		String url = GWT.getModuleBaseURL() + "describe?resourceuri="
				+ uri.replace("#", "%23");
		Window.open(url, "_blank", "");

	}
}
