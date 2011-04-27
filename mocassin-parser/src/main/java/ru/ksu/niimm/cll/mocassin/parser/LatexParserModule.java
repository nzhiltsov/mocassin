package ru.ksu.niimm.cll.mocassin.parser;

import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.NumberingProcessor;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.NumberingProcessorImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.StructureBuilderImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;

import com.google.inject.AbstractModule;

public class LatexParserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Parser.class).to(LatexParserImpl.class);
		bind(StructureBuilder.class).to(StructureBuilderImpl.class);
		bind(NumberingProcessor.class).to(NumberingProcessorImpl.class);
	}

}
