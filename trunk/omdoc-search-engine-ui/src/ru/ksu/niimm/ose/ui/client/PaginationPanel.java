package ru.ksu.niimm.ose.ui.client;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.ose.ui.client.widget.event.PageLinkEvent;
import ru.ksu.niimm.ose.ui.client.widget.event.PageLinkEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PaginationPanel extends Composite implements ClickHandler {

	private static PaginationPanelUiBinder uiBinder = GWT
			.create(PaginationPanelUiBinder.class);

	interface PaginationPanelUiBinder extends
			UiBinder<HorizontalPanel, PaginationPanel> {
	}

	@UiField
	FlexTable table;

	private List<PageLinkEventHandler> pageLinkHandlers = new ArrayList<PageLinkEventHandler>();

	private int limit;

	public PaginationPanel() {
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		initWidget(panel);
		table.setVisible(false);
	}

	public void refresh(PagingLoadInfo<?> pagingLoadInfo) {

		int limit = pagingLoadInfo.getPagingLoadConfig().getLimit();
		setLimit(limit);
		int offset = pagingLoadInfo.getPagingLoadConfig().getOffset();
		int collectionSize = pagingLoadInfo.getFullCollectionSize();
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
					widget = new Anchor("" + i);
					((Anchor) widget).addClickHandler(this);
				}
				table.setWidget(0, i, widget);
				i++;

			}
			table.setWidget(0, i, new Anchor(">>"));
		} else {
			table.setVisible(false);
		}
	}

	public void addPageLinkEventHandler(PageLinkEventHandler handler) {
		this.pageLinkHandlers.add(handler);
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO : create paging load config
		if (event.getSource() instanceof Anchor) {
			Anchor anchor = (Anchor) event.getSource();
			int chosenNumber = Integer.parseInt(anchor.getText());
			PageLinkEvent pageLinkEvent = new PageLinkEvent();
			PagingLoadConfig pagingLoadConfig = new PagingLoadConfig();
			int offset = (chosenNumber - 1) * getLimit();
			pagingLoadConfig.setLimit(getLimit());
			pagingLoadConfig.setOffset(offset);
			pageLinkEvent.setPagingLoadConfig(pagingLoadConfig);
			firePageLinkEvent(pageLinkEvent);
		}

	}

	public List<PageLinkEventHandler> getPageLinkHandlers() {
		return pageLinkHandlers;
	}

	private void firePageLinkEvent(PageLinkEvent pageLinkEvent) {
		for (PageLinkEventHandler handler : getPageLinkHandlers()) {
			handler.handlePageLinkEvent(pageLinkEvent);
		}

	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
