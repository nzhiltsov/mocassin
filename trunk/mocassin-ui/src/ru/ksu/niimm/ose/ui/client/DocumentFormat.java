package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

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
		String url = GWT.getModuleBaseURL() + "download?url=" + uri;
		Window.open(url, "_blank", "");
	}
}
