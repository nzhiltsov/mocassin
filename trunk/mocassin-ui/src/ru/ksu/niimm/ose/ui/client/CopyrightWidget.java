package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CopyrightWidget extends Composite {

	private static CopyrightWidgetUiBinder uiBinder = GWT
			.create(CopyrightWidgetUiBinder.class);
	private MocassinConstants constants = GWT.create(MocassinConstants.class);

	interface CopyrightWidgetUiBinder extends
			UiBinder<HorizontalPanel, CopyrightWidget> {
	}

	@UiField
	HTML message;

	public CopyrightWidget() {
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		initWidget(panel);
		message.setHTML(constants.authorName() + "&nbsp;©2010 Mocassin:&nbsp;"
				+ constants.version());
	}

}
