package ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl;

import java.util.Map;

import com.aliasi.matrix.Vector;

import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LSIndex;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public class LSIndexImpl implements LSIndex {
	private Map<Reference, Vector> referenceVectors;
	private Map<String, Vector> termVectors;

	public LSIndexImpl(Map<Reference, Vector> referenceVectors,
			Map<String, Vector> termVectors) {
		this.referenceVectors = referenceVectors;
		this.termVectors = termVectors;
	}

	public Map<Reference, Vector> getReferenceVectors() {
		return referenceVectors;
	}

	public Map<String, Vector> getTermVectors() {
		return termVectors;
	}

}
