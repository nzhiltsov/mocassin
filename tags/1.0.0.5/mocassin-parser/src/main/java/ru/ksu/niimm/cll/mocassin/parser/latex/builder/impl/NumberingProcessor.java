package ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl;

import java.util.SortedSet;

import ru.ksu.niimm.cll.mocassin.parser.Node;

public interface NumberingProcessor {
	/**
	 * this <b>updates</b> the title of each segment in a given set according to
	 * the 'within-section-numbering' strategy
	 * 
	 * @param sortedNodes
	 */
	void processWithinSectionNumbers(SortedSet<Node> sortedNodes);
	/**
	 * this <b>updates</b> the title of each segment in a given set according to
	 * the 'consecutive-numbering' strategy
	 * 
	 * @param sortedNodes
	 */
	void processConsecutiveNumbers(SortedSet<Node> sortedNodes);
}
