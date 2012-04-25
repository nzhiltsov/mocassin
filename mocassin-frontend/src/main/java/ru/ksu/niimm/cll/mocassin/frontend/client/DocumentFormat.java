package ru.ksu.niimm.cll.mocassin.frontend.client;

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
	enum Action {
		ARXIV, DESCRIBE
	}

	interface Binder extends UiBinder<HorizontalPanel, DocumentFormat> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	Anchor link;

	private String resourceUri;
	/**
	 * string that represents an action performed after click on the link. This
	 * is used via generating appropriate link, @see
	 * {@link DocumentFormat#handleClick(ClickEvent)}
	 */
	private Action action;

	public DocumentFormat() {
		initWidget(binder.createAndBindUi(this));
	}

	public void setText(String text) {
		link.setText(text);
	}

	public void setResourceUri(String resourceUri) {
		this.resourceUri = resourceUri;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@UiHandler("link")
	void handleClick(ClickEvent event) {
		if (action == Action.DESCRIBE) {
			String encodedResourceUri = resourceUri.replaceFirst("#", "%23");
			String debugParam = GWT.isScript() ? ""
					: "&gwt.codesvr=127.0.0.1:9997";
			String url = GWT.getHostPageBaseURL() + "describe?resourceuri="
					+ encodedResourceUri + debugParam;
			Window.open(url, "_blank", "");
		} else if (action == Action.ARXIV) {
			Window.open(resourceUri, "_blank", "");
		}

	}
}
