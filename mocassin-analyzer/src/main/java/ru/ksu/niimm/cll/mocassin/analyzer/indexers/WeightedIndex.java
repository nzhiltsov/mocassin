package ru.ksu.niimm.cll.mocassin.analyzer.indexers;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import com.aliasi.matrix.Vector;

public interface WeightedIndex {
	Map<Reference, Vector> getReferenceVectors();
	
	List<String> getTerms();
}
