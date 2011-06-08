package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StructureViewer implements EntryPoint {
	interface Binder extends UiBinder<ScrollPanel, StructureViewer> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private final ViewerServiceAsync viewerService = GWT
			.create(ViewerService.class);

	private ViewerConstants constants = GWT.create(ViewerConstants.class);
	@UiField
	Frame frame;
	@UiField
	CaptionPanel metadataCaptionPanel;
	@UiField
	CaptionPanel documentStructureGraphPanel;
	@UiField
	VerticalPanel graphPanel;
	@UiField
	CheckBox hasPartCheckbox;
	@UiField
	CheckBox refersToCheckbox;
	@UiField
	CheckBox dependsOnCheckbox;
	@UiField
	CheckBox provesCheckbox;
	@UiField
	CheckBox hasConsequenceCheckbox;
	@UiField
	CheckBox exemplifiesCheckbox;

	public void onModuleLoad() {
		ScrollPanel outer = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		root.forceLayout();

		String resourceUri = Location.getParameter("resourceuri");
		String pdfUri = Location.getParameter("pdfuri");
		if (resourceUri == null || pdfUri == null)
			return;

		metadataCaptionPanel.setCaptionText(constants.metadataPanelTitle());

		viewerService.load(resourceUri, new LoadMetadataCallback());

	}

	private class LoadMetadataCallback implements AsyncCallback<ArticleInfo> {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert("couldn't load the metadata: " + caught.getMessage());

		}

		@Override
		public void onSuccess(ArticleInfo result) {
			VerticalPanel metadataPanel = new VerticalPanel();
			metadataPanel.setSpacing(5);

			metadataPanel.setSize("100%", "180");

			String key = result.getKey() != null ? result.getKey() : "";
			Label lblNewLabel = new Label("arXiv: " + key);
			lblNewLabel.setStyleName("paper-id");
			metadataPanel.add(lblNewLabel);

			Label lblNewLabel_1 = new Label(result.getTitle());
			lblNewLabel_1.setStyleName("paper-title");
			metadataPanel.add(lblNewLabel_1);

			List<String> authors = result.getAuthors();
			for (String authorName : authors) {
				Label lblNewLabel_2 = new Label(authorName);
				lblNewLabel_2.setStyleName("paper-author");
				metadataPanel.add(lblNewLabel_2);
			}

			if (result.getCurrentPageNumber() > 0) {
				int currentPageNumber = result.getCurrentPageNumber() - 1;
				String url = "http://docs.google.com/viewer?url="
						+ result.getPdfUri() + "&embedded=true#:0.page."
						+ currentPageNumber;
				frame.setUrl(url);
			} else {
				frame.setUrl("http://docs.google.com/viewer?url="
						+ result.getPdfUri() + "&embedded=true");
			}

			metadataCaptionPanel.add(metadataPanel);
			documentStructureGraphPanel.setCaptionText(constants
					.graphPanelTitle());

			DocumentStructureGraph documentStructureGraph = new DocumentStructureGraph(
					frame, hasPartCheckbox, refersToCheckbox, dependsOnCheckbox, provesCheckbox, hasConsequenceCheckbox, exemplifiesCheckbox, result);
			documentStructureGraph.setHeight("450");
			graphPanel.add(documentStructureGraph.asWidget());
		}

	}
}