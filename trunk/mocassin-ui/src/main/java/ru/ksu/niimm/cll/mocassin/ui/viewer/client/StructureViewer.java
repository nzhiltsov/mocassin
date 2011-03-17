package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.CssResource.Strict;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StructureViewer implements EntryPoint {
	
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setSize("1024", "768");

		VerticalPanel documentPanel = new VerticalPanel();
		rootPanel.add(documentPanel, 0, 0);
		documentPanel.setSize("700", "100%");

		final Frame frame = new Frame(
				"http://docs.google.com/viewer?url=http://arxiv.org/pdf/1103.2935v1&embedded=true");
		documentPanel.add(frame);
		frame.setSize("100%", "100%");

		VerticalPanel structurePanel = new VerticalPanel();
		rootPanel.add(structurePanel, 706, 0);
		structurePanel.setSize("280px", "100%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setBorderWidth(1);
		verticalPanel.setSpacing(5);
		structurePanel.add(verticalPanel);
		verticalPanel.setSize("100%", "30%");

		Label lblNewLabel = new Label(" arXiv:1103.2935");
		lblNewLabel.setStyleName("paper-id");
		verticalPanel.add(lblNewLabel);

		Label lblNewLabel_1 = new Label(
				"Involutive distributions and dynamical systems of second-order type");
		lblNewLabel_1.setStyleName("paper-title");
		verticalPanel.add(lblNewLabel_1);

		Label lblNewLabel_2 = new Label("T. Mestdag, M. Crampin");
		lblNewLabel_2.setStyleName("paper-author");
		verticalPanel.add(lblNewLabel_2);

		Hyperlink hprlnkNewHyperlink = new Hyperlink("Page 2", false, "");
		hprlnkNewHyperlink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				frame
						.setUrl("http://docs.google.com/viewer?url=http://arxiv.org/pdf/1103.2935v1&embedded=true#:0.page.1");
			}
		});

		VerticalPanel htmlNewHtml = new VerticalPanel();
		htmlNewHtml.setSize("280", "280");
		structurePanel.add(hprlnkNewHyperlink);
	}
	
}