package ru.ksu.niimm.cll.mocassin.frontend.common.client;


public class PageLinkEvent {
	private PagingLoadConfig pagingLoadConfig;

	public PagingLoadConfig getPagingLoadConfig() {
		return pagingLoadConfig;
	}

	public void setPagingLoadConfig(PagingLoadConfig pagingLoadConfig) {
		this.pagingLoadConfig = pagingLoadConfig;
	}

}
