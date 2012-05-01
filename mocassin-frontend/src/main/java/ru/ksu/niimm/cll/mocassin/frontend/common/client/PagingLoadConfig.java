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

@SuppressWarnings("serial")
public class PagingLoadConfig implements Serializable {
	/**
	 * count of elements that should be loaded
	 */
	private int limit;
	/**
	 * starts with 0
	 */
	private int offset;

	public PagingLoadConfig() {

	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public static PagingLoadConfig adjustPagingLoadConfig(
			PagingLoadConfig pagingLoadConfig, int collectionSize) {
		int givenOffset = pagingLoadConfig.getOffset();
		int givenLimit = pagingLoadConfig.getLimit();

		if (givenOffset < 0) {
			givenOffset = 0;
		}
		if (givenOffset >= collectionSize) {
			givenOffset = collectionSize - 1;
		}
		if (givenLimit < 0) {
			givenLimit = 0;
		}
		if (givenOffset + givenLimit > collectionSize) {
			givenLimit = collectionSize - givenOffset;
		}
		PagingLoadConfig adjustedPagingLoadConfig = new PagingLoadConfig();
		adjustedPagingLoadConfig.setOffset(givenOffset);
		adjustedPagingLoadConfig.setLimit(givenLimit);
		return adjustedPagingLoadConfig;
	}

	@Override
	public String toString() {
		return "PagingLoadConfig [limit=" + limit + ", offset=" + offset + "]";
	}

}
