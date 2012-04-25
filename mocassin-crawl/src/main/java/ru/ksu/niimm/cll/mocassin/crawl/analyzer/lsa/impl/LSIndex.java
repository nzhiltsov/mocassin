package ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.indexers.Index;

import com.aliasi.matrix.Vector;

public interface LSIndex extends Index {

	Map<String, Vector> getTermVectors();
}
