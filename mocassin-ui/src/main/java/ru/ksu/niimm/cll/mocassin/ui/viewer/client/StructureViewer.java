package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StructureViewer implements EntryPoint {
	private final ViewerServiceAsync viewerService = GWT
			.create(ViewerService.class);
	private CaptionPanel metadataCaptionPanel;

	public void onModuleLoad() {
		String resourceUri = Location.getParameter("resourceuri");
		String pdfUri = Location.getParameter("pdfuri");
		if (resourceUri == null || pdfUri == null)
			return;

		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("1024", "768");

		VerticalPanel documentPanel = new VerticalPanel();
		rootPanel.add(documentPanel, 0, 0);
		documentPanel.setSize("700", "100%");

		final Frame frame = new Frame("http://docs.google.com/viewer?url="
				+ pdfUri + "&embedded=true");
		documentPanel.add(frame);
		frame.setSize("100%", "100%");

		VerticalPanel structurePanel = new VerticalPanel();
		structurePanel.setSpacing(5);
		rootPanel.add(structurePanel, 706, 0);
		structurePanel.setSize("280px", "100%");

		metadataCaptionPanel = new CaptionPanel();
		metadataCaptionPanel.setCaptionText("Metadata");
		structurePanel.add(metadataCaptionPanel);
		viewerService.load(resourceUri, new LoadMetadataCallback());

		final CaptionPanel documentStructureGraphPanel = new CaptionPanel();
		documentStructureGraphPanel
				.setCaptionText("Graph of the Document Structure");
		structurePanel.add(documentStructureGraphPanel);

		DocumentStructureGraph documentStructureGraph = new DocumentStructureGraph();
		documentStructureGraph.setHeight("350");
		documentStructureGraphPanel.add(documentStructureGraph.asWidget());
		documentStructureGraph.setFrame(frame);

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

			metadataPanel.setSize("100%", "150");

			String key = result.getKey() != null ? result.getKey() : "";
			Label lblNewLabel = new Label("arXiv:" + key);
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

			metadataCaptionPanel.add(metadataPanel);

		}

	}
}