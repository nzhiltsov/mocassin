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

	private static final String FORWARD_LINK_STRING = ">>";

	private static final String BACK_LINK_STRING = "<<";

	private static PaginationPanelUiBinder uiBinder = GWT
			.create(PaginationPanelUiBinder.class);

	interface PaginationPanelUiBinder extends
			UiBinder<HorizontalPanel, PaginationPanel> {
	}

	@UiField
	FlexTable table;

	private List<PageLinkEventHandler> pageLinkHandlers = new ArrayList<PageLinkEventHandler>();

	private int limit;
	/**
	 * current page number
	 * 
	 * starts with 1
	 */
	private int currentPageNumber;

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
		setCurrentPageNumber(currentPageNumber);
		if (pagesNumber > 1) {
			table.setVisible(true);
			table.clear();
			Anchor backLink = new Anchor(BACK_LINK_STRING);
			backLink.addClickHandler(this);
			backLink.setVisible(getCurrentPageNumber() != 1);
			table.setWidget(0, 0, backLink);
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
			Anchor forwardLink = new Anchor(FORWARD_LINK_STRING);
			forwardLink.addClickHandler(this);
			forwardLink.setVisible(getCurrentPageNumber() != pagesNumber);
			table.setWidget(0, i, forwardLink);
		} else {
			table.setVisible(false);
		}
	}

	public void addPageLinkEventHandler(PageLinkEventHandler handler) {
		this.pageLinkHandlers.add(handler);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() instanceof Anchor) {
			Anchor anchor = (Anchor) event.getSource();
			String anchorText = anchor.getText();

			PagingLoadConfig pagingLoadConfig = new PagingLoadConfig();
			int offset;
			if (anchor.getText().equals(BACK_LINK_STRING)) {
				offset = (getCurrentPageNumber() - 2) * getLimit();
			} else if (anchor.getText().equals(FORWARD_LINK_STRING)) {
				offset = (getCurrentPageNumber()) * getLimit();
			} else {
				int chosenNumber = Integer.parseInt(anchorText);
				offset = (chosenNumber - 1) * getLimit();
			}
			PageLinkEvent pageLinkEvent = new PageLinkEvent();
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

	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

}
