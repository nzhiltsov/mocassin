package ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.WeightedIndex;

import com.aliasi.matrix.Vector;

public interface LSIndex extends WeightedIndex {

	Map<String, Vector> getTermVectors();
}
