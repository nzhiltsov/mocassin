package ru.ksu.niimm.cll.mocassin.crawl.analyzer.indexers;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;


import com.aliasi.matrix.Vector;

public interface Index {
	Map<Reference, Vector> getReferenceVectors();
	
	List<String> getTerms();
}
