package ru.ksu.niimm.ose.ui.client;

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

	public PagingLoadConfig(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
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

	public static boolean isValid(PagingLoadConfig pagingLoadConfig,
			int collectionSize) {
		int givenOffset = pagingLoadConfig.getOffset();
		int givenLimit = pagingLoadConfig.getLimit();
		if (pagingLoadConfig != null && givenOffset >= 0
				&& givenOffset < collectionSize && givenLimit >= 0
				&& givenLimit <= collectionSize) {
			return true;
		}
		return false;

	}

	@Override
	public String toString() {
		return String.format("PagingLoadConfig [limit=%d, offset=%d]", limit,
				offset);
	}

}
