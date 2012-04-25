package ru.ksu.niimm.cll.mocassin.frontend.client;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.MocassinConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class CopyrightWidget extends Composite {

	private static CopyrightWidgetUiBinder uiBinder = GWT
			.create(CopyrightWidgetUiBinder.class);
	private MocassinConstants constants = GWT.create(MocassinConstants.class);

	interface CopyrightWidgetUiBinder extends
			UiBinder<HorizontalPanel, CopyrightWidget> {
	}

	public CopyrightWidget() {
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		initWidget(panel);

	}

}
