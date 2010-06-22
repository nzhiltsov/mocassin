package ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.Analyzer;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.AnalyzersProvider;

public class AnalyzersProviderImpl implements AnalyzersProvider {

	@Override
	public List<Analyzer> get() {
		List<Analyzer> analyzers = new ArrayList<Analyzer>();
		analyzers.add(new ContainmentAnalyzer());
		return Collections.unmodifiableList(analyzers);
	}

}
