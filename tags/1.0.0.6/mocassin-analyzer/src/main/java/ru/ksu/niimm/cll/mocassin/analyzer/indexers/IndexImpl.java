package ru.ksu.niimm.cll.mocassin.analyzer.indexers;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import com.aliasi.matrix.Vector;

public class IndexImpl implements Index {
	private Map<Reference, Vector> referenceVectors;
	private List<String> terms;

	public IndexImpl(Map<Reference, Vector> referenceVectors,
			List<String> terms) {
		this.referenceVectors = referenceVectors;
		this.terms = terms;
	}

	public Map<Reference, Vector> getReferenceVectors() {
		return referenceVectors;
	}

	public List<String> getTerms() {
		return terms;
	}

}