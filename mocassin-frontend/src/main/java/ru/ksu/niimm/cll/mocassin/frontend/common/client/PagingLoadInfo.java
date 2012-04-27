package ru.ksu.niimm.cll.mocassin.frontend.common.client;

import java.io.Serializable;
import java.util.Collection;

@SuppressWarnings("serial")
public class PagingLoadInfo<T extends Serializable> implements Serializable {
	private PagingLoadConfig pagingLoadConfig;
	private Collection<T> data;
	private int fullCollectionSize;
	public PagingLoadConfig getPagingLoadConfig() {
		return pagingLoadConfig;
	}

	public void setPagingLoadConfig(PagingLoadConfig pagingLoadConfig) {
		this.pagingLoadConfig = pagingLoadConfig;
	}

	public Collection<T> getData() {
		return data;
	}

	public void setData(Collection<T> data) {
		this.data = data;
	}

	public int getFullCollectionSize() {
		return fullCollectionSize;
	}

	public void setFullCollectionSize(int fullCollectionSize) {
		this.fullCollectionSize = fullCollectionSize;
	}

}