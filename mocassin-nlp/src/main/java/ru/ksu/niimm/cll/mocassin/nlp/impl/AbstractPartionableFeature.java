package ru.ksu.niimm.cll.mocassin.nlp.impl;


public class AbstractPartionableFeature {

	protected String[] left;

	protected String[] right;

	public AbstractPartionableFeature(String[] left, String[] right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < left.length; i++) {
			String value = left[i] != null ? left[i] : "";
			sb.append(value);
			if (i < left.length - 1) {
				sb.append("|");
			}

		}
		sb.append("|");
		for (int i = 0; i < right.length; i++) {
			String value = right[i] != null ? right[i] : "";
			sb.append(value);
			if (i < right.length - 1) {
				sb.append("|");
			}

		}
		return sb.toString();
	}

}
