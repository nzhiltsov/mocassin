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

public class NavigationPopup extends Composite {
	interface Binder extends UiBinder<HorizontalPanel, NavigationPopup> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private ViewerConstants constants = GWT.create(ViewerConstants.class);

	private Frame frame;

	private int numPage;

	private String pdfUri;

	@UiField
	PopupPanel popup;
	@UiField
	Anchor goLink;

	public NavigationPopup(Frame frame, String pdfUri) {
		initWidget(binder.createAndBindUi(this));
		this.popup.setAutoHideEnabled(true);
		
		this.frame = frame;
		this.pdfUri = pdfUri;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
		goLink.setText(constants.goLink() + " " + this.numPage);
	}

	public void setPopupPosition(int left, int top) {
		popup.setPopupPosition(left, top);
	}

	public void show() {
		if (this.numPage > 0) {
			popup.show();
		}
	}

	@UiHandler("goLink")
	void handleClick(ClickEvent event) {
		int urlNumPage = this.numPage - 1;
		frame.setUrl("http://docs.google.com/viewer?url=" + pdfUri
				+ "&embedded=true#:0.page." + urlNumPage);
		popup.hide();
	}
}
