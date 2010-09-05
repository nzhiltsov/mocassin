package ru.ksu.niimm.cll.mocassin.nlp.impl;

import ru.ksu.niimm.cll.mocassin.nlp.Feature;

public class PosFeature extends AbstractPartionableFeature implements Feature {

	public PosFeature(String[] left, String[] right) {
		super(left, right);
	}

	public String[] getLeftPosTags() {
		return left;
	}

	public String[] getRightPosTags() {
		return right;
	}
}
