/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
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
