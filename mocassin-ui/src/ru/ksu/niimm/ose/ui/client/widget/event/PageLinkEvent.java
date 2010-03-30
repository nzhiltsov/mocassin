package ru.ksu.niimm.ose.ui.client.widget.event;

import ru.ksu.niimm.ose.ui.client.PagingLoadConfig;

public class PageLinkEvent {
	private PagingLoadConfig pagingLoadConfig;

	public PagingLoadConfig getPagingLoadConfig() {
		return pagingLoadConfig;
	}

	public void setPagingLoadConfig(PagingLoadConfig pagingLoadConfig) {
		this.pagingLoadConfig = pagingLoadConfig;
	}

}
