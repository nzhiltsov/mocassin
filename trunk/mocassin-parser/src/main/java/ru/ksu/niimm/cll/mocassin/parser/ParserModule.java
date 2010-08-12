package ru.ksu.niimm.cll.mocassin.parser;

import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.BuildersProvider;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.BuildersProviderImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.TreeParserImpl;

import com.google.inject.AbstractModule;

public class ParserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Parser.class).to(LatexParserImpl.class);
		bind(TreeParser.class).to(TreeParserImpl.class);
		bind(BuildersProvider.class).to(BuildersProviderImpl.class);


	}

}
