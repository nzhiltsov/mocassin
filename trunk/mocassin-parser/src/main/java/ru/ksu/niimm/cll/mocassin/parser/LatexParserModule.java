package ru.ksu.niimm.cll.mocassin.parser;

import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.StructureBuilderImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;

import com.google.inject.AbstractModule;

public class LatexParserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Parser.class).to(LatexParserImpl.class);
		bind(StructureBuilder.class).to(StructureBuilderImpl.class);
	}

}
