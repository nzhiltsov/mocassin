package ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.WeightedIndexImpl;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import com.aliasi.matrix.Vector;

public class LSIndexImpl extends WeightedIndexImpl implements LSIndex {

	private Map<String, Vector> termVectors;

	public LSIndexImpl(Map<Reference, Vector> referenceVectors,
			Map<String, Vector> termVectors, List<String> terms) {
		super(referenceVectors, terms);
		this.termVectors = termVectors;
	}

	public Map<String, Vector> getTermVectors() {
		return termVectors;
	}

}
