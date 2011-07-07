package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class NavigationPopup extends PopupPanel {
	interface Binder extends UiBinder<NavigationPopup, NavigationPopup> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private ViewerConstants constants = GWT.create(ViewerConstants.class);

	private int numPage;

	private String pdfUri;
	@UiField
	Anchor goLink;

	public NavigationPopup() {
		binder.createAndBindUi(this);
		setAutoHideEnabled(true);
	}

	public void setPdfUri(String pdfUri) {
		this.pdfUri = pdfUri;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
		goLink.setText(constants.goLink() + " " + this.numPage);
	}

	public void show() {
		if (this.numPage > 0) {
			show();
		}
	}

	@UiHandler("goLink")
	void handleClick(ClickEvent event) {
		int urlNumPage = this.numPage - 1;
		/*frame.setUrl("http://docs.google.com/viewer?url=" + pdfUri
				+ "&embedded=true#:0.page." + urlNumPage);*/
		hide();
	}
}
