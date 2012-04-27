package ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl;

import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.indexers.IndexImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

import com.aliasi.matrix.Vector;

public class LSIndexImpl extends IndexImpl implements LSIndex {

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