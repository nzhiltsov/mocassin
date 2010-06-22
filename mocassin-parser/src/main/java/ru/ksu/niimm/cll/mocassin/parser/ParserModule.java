package ru.ksu.niimm.cll.mocassin.parser;

import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.AnalyzersProvider;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.impl.AnalyzersProviderImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.TreeParserImpl;

import com.google.inject.AbstractModule;


public class ParserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Parser.class).to(LatexParserImpl.class);
		bind(TreeParser.class).to(TreeParserImpl.class);
		bind(AnalyzersProvider.class).to(AnalyzersProviderImpl.class);
	}

}
