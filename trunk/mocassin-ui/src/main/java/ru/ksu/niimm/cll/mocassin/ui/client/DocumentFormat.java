package ru.ksu.niimm.cll.mocassin.ui.client;

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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

public class DocumentFormat extends Composite {
	interface Binder extends UiBinder<HorizontalPanel, DocumentFormat> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	Anchor link;
	@UiField
	FormPanel form;
	@UiField
	Hidden resourceuri;

	// private String uri;

	public DocumentFormat() {
		initWidget(binder.createAndBindUi(this));
		form.setAction("/mocassin/describe");
		resourceuri.setName("resourceuri");
	}

	public void setText(String text) {
		link.setText(text);
	}

	public void setUri(String uri) {
		// this.uri = uri;
		this.resourceuri.setValue(uri);
	}

	@UiHandler("link")
	void handleClick(ClickEvent event) {
		form.submit();
		/*
		 * String url = GWT.getModuleBaseURL() + "describe?resourceuri=" + uri;
		 * Window.open(url, "_blank", "");
		 */
	}
}
