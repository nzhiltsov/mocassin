package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class PaginationPanel extends Composite {

	private static PaginationPanelUiBinder uiBinder = GWT
			.create(PaginationPanelUiBinder.class);

	interface PaginationPanelUiBinder extends
			UiBinder<HorizontalPanel, PaginationPanel> {
	}

	@UiField
	FlexTable table;

	public PaginationPanel() {
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		initWidget(panel);

		table.setWidget(0, 0, new Anchor("<<"));
		table.setWidget(0, 1, new Anchor("1"));
		table.setWidget(0, 2, new Anchor("2"));
		table.setWidget(0, 3, new Anchor("3"));
		table.setWidget(0, 4, new Anchor(">>"));
	}

	public void refresh(PagingLoadConfig pagingLoadConfig) {
		// TODO : need to refresh panel
	}

}
