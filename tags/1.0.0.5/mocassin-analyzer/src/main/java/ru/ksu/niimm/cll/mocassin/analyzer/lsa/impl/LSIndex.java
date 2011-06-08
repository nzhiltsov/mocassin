package ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.Index;

import com.aliasi.matrix.Vector;

public interface LSIndex extends Index {

	Map<String, Vector> getTermVectors();
}
