package ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.latex.builder.Builder;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.AnalyzersProvider;

public class BuildersProviderImpl implements AnalyzersProvider {

	@Override
	public List<Builder> get() {
		List<Builder> analyzers = new ArrayList<Builder>();
		analyzers.add(new StructureBuilder());
		return Collections.unmodifiableList(analyzers);
	}

}
