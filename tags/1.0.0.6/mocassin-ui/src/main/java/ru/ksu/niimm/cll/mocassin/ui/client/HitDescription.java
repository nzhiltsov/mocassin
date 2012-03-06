package ru.ksu.niimm.cll.mocassin.ui.client;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ui.client.DocumentFormat.Action;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HitDescription extends Composite {
	interface Binder extends UiBinder<VerticalPanel, HitDescription> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	HorizontalPanel titlePanel;
	@UiField
	Label authorLabel;
	@UiField
	Anchor titleLink;
	@UiField
	DocumentFormat rdfDocumentFormat;
	@UiField
	DocumentFormat viewerDocumentFormat;
	@UiField
	Label relevantContextLabel;

	private String viewerUri;

	private String pdfUri;

	@UiConstructor
	public HitDescription(ResultDescription resultDescription) {
		initWidget(binder.createAndBindUi(this));
		viewerUri = resultDescription.getViewerUri();
		pdfUri = resultDescription.getPdfUri();
		titleLink.setText(resultDescription.getTitle());
		viewerDocumentFormat.setText("Original");
		viewerDocumentFormat.setResourceUri(resultDescription.getDocumentUri());
		viewerDocumentFormat.setAction(Action.ARXIV);
		rdfDocumentFormat.setText("RDF");
		rdfDocumentFormat.setAction(Action.DESCRIBE);
		rdfDocumentFormat.setResourceUri(getViewerUri());
		relevantContextLabel.setText(resultDescription
				.getRelevantContextString());

		List<String> authors = resultDescription.getAuthors();
		String authorLabelText = "";
		for (int i = 0; i < authors.size(); i++) {
			authorLabelText += authors.get(i);
			if (i + 1 < authors.size()) {
				authorLabelText += ", ";
			}
		}
		authorLabel.setText(authorLabelText);
	}

	public String getViewerUri() {
		return viewerUri;
	}

	public String getPdfUri() {
		return pdfUri;
	}

	@UiHandler("titleLink")
	void handleClick(ClickEvent event) {
		String encodedResourceUri = getViewerUri().replaceFirst("#", "%23");
		String debugParam = GWT.isScript() ? "" : "&gwt.codesvr=127.0.0.1:9997";
		String url = GWT.getHostPageBaseURL()
				+ "StructureViewer.html?resourceuri=" + encodedResourceUri
				+ "&pdfuri=" + pdfUri + debugParam;
		Window.open(url, "_blank", "");
	}
}
