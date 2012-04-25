package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StructureViewer implements EntryPoint, NavigationEventHandler {
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
	DocumentStructureGraphPanel documentStructureGraphPanel;

	private String currentArxivId;

	public void onModuleLoad() {
		ScrollPanel outer = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		root.forceLayout();

		String resourceUri = Location.getParameter("resourceuri");
		if (resourceUri == null)
			return;

		App.eventBus.addHandler(NavigationEvent.TYPE, this);

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
			currentArxivId = result.getKey();
			VerticalPanel metadataPanel = new VerticalPanel();
			metadataPanel.setSpacing(5);

			metadataPanel.setSize("100%", "180");

			String key = currentArxivId != null ? currentArxivId : "";
			Label lblNewLabel = new Label("Article id: " + key);
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

			int currentPageNumber = result.getCurrentPageNumber() > 0 ? result
					.getCurrentPageNumber() - 1 : 0;

			String currentSegmentUri = result.getCurrentSegmentUri();
			String url = assembleUrl(currentSegmentUri, currentPageNumber);
			refreshFrame(url);

			metadataCaptionPanel.add(metadataPanel);
			documentStructureGraphPanel.refresh(result);
		}
	}

	private String assembleUrl(String currentSegmentUri, int currentPageNumber) {
		String url = "http://docs.google.com/viewer?url=http://cll.niimm.ksu.ru/mocassin/"
				+ "mocassin/download/arxivid/"
				+ currentArxivId
				+ (currentSegmentUri != null ? "$"
						+ currentSegmentUri.substring(currentSegmentUri
								.lastIndexOf("/") + 1) : "")
				+ "&embedded=true#:0.page." + currentPageNumber;
		return url;
	}

	@Override
	public void onChange(final NavigationEvent navEvent) {
		refreshFrame(assembleUrl(navEvent.getCurrentUri(),
				navEvent.getNumPage()));
	}
	
	private void refreshFrame(final String url) {
		frame.setUrl(url);
		// using Timer is a workaround to refresh the frame
		Timer timer = new Timer() {
			@Override
			public void run() {
				frame.setUrl(url);
				cancel();
			}
		};
		timer.schedule(3000);
	}
}