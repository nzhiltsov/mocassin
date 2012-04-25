package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Feature;

public class WordFeature extends AbstractPartionableFeature implements Feature {

	public WordFeature(String[] left, String[] right) {
		super(left, right);
	}

	public String[] getLeftWords() {
		return left;
	}

	public String[] getRightWords() {
		return right;
	}
}
