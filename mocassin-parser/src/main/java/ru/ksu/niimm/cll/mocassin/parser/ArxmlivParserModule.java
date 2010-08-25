package ru.ksu.niimm.cll.mocassin.parser;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivParserImpl;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.XPathSearcher;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.XPathSearcherImpl;

import com.google.inject.AbstractModule;

public class ArxmlivParserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Parser.class).to(ArxmlivParserImpl.class);
		bind(XPathSearcher.class).to(XPathSearcherImpl.class);
	}

}
