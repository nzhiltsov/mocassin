package ru.ksu.niimm.ose.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
		table.setVisible(false);
		table.setWidget(0, 0, new Anchor("<<"));
		table.setWidget(0, 1, new Anchor("1"));
		table.setWidget(0, 2, new Anchor("2"));
		table.setWidget(0, 3, new Anchor("3"));
		table.setWidget(0, 4, new Anchor(">>"));
	}

	public void refresh(PagingLoadInfo<?> pagingLoadInfo) {

		int limit = pagingLoadInfo.getPagingLoadConfig().getLimit();
		int offset = pagingLoadInfo.getPagingLoadConfig().getOffset();
		int collectionSize = pagingLoadInfo.getData().size();
		int pagesNumber = collectionSize % limit > 0 ? collectionSize / limit
				+ 1 : collectionSize / limit;
		int currentPageNumber = offset / limit + 1;
		if (pagesNumber > 1) {
			table.setVisible(true);
			table.clear();
			table.setWidget(0, 0, new Anchor("<<"));
			int i = 1;
			while (i <= pagesNumber) {
				Widget widget;
				if (currentPageNumber == i) {
					widget = new Label("" + currentPageNumber);
				} else {
					widget = new Anchor("");
				}
				table.setWidget(0, i, widget);
				i++;

			}
		} else {
			table.setVisible(false);
		}
	}
}
