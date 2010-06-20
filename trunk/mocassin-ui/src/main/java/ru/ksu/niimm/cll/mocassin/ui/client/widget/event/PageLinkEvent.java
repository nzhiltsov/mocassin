package ru.ksu.niimm.cll.mocassin.ui.client.widget.event;

import ru.ksu.niimm.cll.mocassin.ui.client.PagingLoadConfig;

public class PageLinkEvent {
	private PagingLoadConfig pagingLoadConfig;

	public PagingLoadConfig getPagingLoadConfig() {
		return pagingLoadConfig;
	}

	public void setPagingLoadConfig(PagingLoadConfig pagingLoadConfig) {
		this.pagingLoadConfig = pagingLoadConfig;
	}

}
