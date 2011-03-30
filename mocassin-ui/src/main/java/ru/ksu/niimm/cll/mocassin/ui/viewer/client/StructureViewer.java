package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StructureViewer implements EntryPoint {

	public void onModuleLoad() {
		String resourceUri = Location.getParameter("resourceuri");
		if (resourceUri == null)
			return;

		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("1024", "768");

		VerticalPanel documentPanel = new VerticalPanel();
		rootPanel.add(documentPanel, 0, 0);
		documentPanel.setSize("700", "100%");

		final Frame frame = new Frame("http://docs.google.com/viewer?url="
				+ resourceUri + "&embedded=true");
		documentPanel.add(frame);
		frame.setSize("100%", "100%");

		VerticalPanel structurePanel = new VerticalPanel();
		structurePanel.setSpacing(5);
		rootPanel.add(structurePanel, 706, 0);
		structurePanel.setSize("280px", "100%");

		CaptionPanel metadataCaptionPanel = new CaptionPanel();
		metadataCaptionPanel.setCaptionText("Metadata");
		VerticalPanel metadataPanel = new VerticalPanel();
		metadataPanel.setSpacing(5);

		metadataPanel.setSize("100%", "150");

		Label lblNewLabel = new Label("arXiv:math/0205003");
		lblNewLabel.setStyleName("paper-id");
		metadataPanel.add(lblNewLabel);

		Label lblNewLabel_1 = new Label(
				"A strengthening of the Nyman-Beurling criterion for the Riemann hypothesis, 2");
		lblNewLabel_1.setStyleName("paper-title");
		metadataPanel.add(lblNewLabel_1);

		Label lblNewLabel_2 = new Label("Luis Baez-Duarte");
		lblNewLabel_2.setStyleName("paper-author");
		metadataPanel.add(lblNewLabel_2);
		metadataCaptionPanel.add(metadataPanel);
		structurePanel.add(metadataCaptionPanel);

		final CaptionPanel documentStructureGraphPanel = new CaptionPanel();
		documentStructureGraphPanel
				.setCaptionText("Graph of the Document Structure");
		structurePanel.add(documentStructureGraphPanel);

		DocumentStructureGraph documentStructureGraph = new DocumentStructureGraph();
		documentStructureGraph.setHeight("350");
		documentStructureGraphPanel.add(documentStructureGraph.asWidget());
		documentStructureGraph.setFrame(frame);

	}

}