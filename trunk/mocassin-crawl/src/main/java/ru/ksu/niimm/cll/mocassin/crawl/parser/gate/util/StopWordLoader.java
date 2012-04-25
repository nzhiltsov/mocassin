package ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util;

import java.util.Set;

import com.google.common.base.Predicate;

public interface StopWordLoader {
	Set<String> getStopWords();
	
	Predicate<String> getNonStopWordPredicate();
	
	boolean isStopWord(String word);
}
