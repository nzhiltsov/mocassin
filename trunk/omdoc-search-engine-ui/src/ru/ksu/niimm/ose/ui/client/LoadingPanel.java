package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPanel extends Composite {
	interface Binder extends UiBinder<HorizontalPanel, LoadingPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	PopupPanel popup;

	public LoadingPanel() {
		initWidget(binder.createAndBindUi(this));
		popup.setModal(true);
		popup.setGlassEnabled(true);
	}

	public void popupShow() {
		popup.center();
	}

	public void popupHide() {
		popup.hide();
	}

}
