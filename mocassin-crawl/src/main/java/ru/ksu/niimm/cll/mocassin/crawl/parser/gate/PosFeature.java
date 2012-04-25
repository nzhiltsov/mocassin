package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Feature;

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
