package ru.ksu.niimm.cll.mocassin.ui.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
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
	Hyperlink titleLink;
	@UiField
	DocumentFormat latexDocumentFormat;
	@UiField
	DocumentFormat pdfDocumentFormat;
	@UiField
	Label relevantContextLabel;

	private String documentUri;

	@UiConstructor
	public HitDescription(ResultDescription resultDescription) {
		initWidget(binder.createAndBindUi(this));
		documentUri = resultDescription.getDocumentUri();
		titleLink.setText(resultDescription.getTitle());
		latexDocumentFormat.setText("LaTeX");
		latexDocumentFormat.setUri(resultDescription.getLatexUri());
		pdfDocumentFormat.setText("PDF");
		pdfDocumentFormat.setUri(resultDescription.getPdfUri());
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

	@UiHandler("titleLink")
	void handleClick(ClickEvent event) {
		String url = GWT.getModuleBaseURL() + "download?url="
				+ getDocumentUri();
		Window.open(url, "_blank", "");
	}

	public String getDocumentUri() {
		return documentUri;
	}

}
