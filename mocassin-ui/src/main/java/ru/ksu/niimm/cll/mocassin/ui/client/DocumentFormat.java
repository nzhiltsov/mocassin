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

	private String pdfUri;

	private String resourceUri;
	/**
	 * string that represents an action performed after click on the link. This
	 * is used via generating appropriate link, @see
	 * {@link DocumentFormat#handleClick(ClickEvent)}
	 */
	private String action;

	public DocumentFormat() {
		initWidget(binder.createAndBindUi(this));
	}

	public void setText(String text) {
		link.setText(text);
	}

	public void setPdfUri(String uri) {
		this.pdfUri = uri;
	}

	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	public void setAction(String action) {
		this.action = action.trim();
	}

	@UiHandler("link")
	void handleClick(ClickEvent event) {
		String encodedResourceUri = resourceUri.replaceFirst("#", "%23");
		String debugParam = GWT.isScript() ? "" : "&gwt.codesvr=127.0.0.1:9997";
		String url = GWT.getHostPageBaseURL() + action + "?resourceuri="
				+ encodedResourceUri + "&pdfuri=" + pdfUri + debugParam;
		Window.open(url, "_blank", "");

	}
}
