package ru.ksu.niimm.cll.mocassin.parser;

import ru.ksu.niimm.cll.mocassin.parser.applications.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.parser.applications.importance.impl.ImportantNodeServiceImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.AnalyzersProvider;
import ru.ksu.niimm.cll.mocassin.parser.latex.analyzer.impl.AnalyzersProviderImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.TreeParserImpl;
import ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.impl.NameMatcher;
import ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.impl.NameMatcherPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.parser.mapping.matchers.impl.NameMatcherPropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.parser.similarity.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.parser.similarity.impl.StringSimilarityEvaluatorImpl;

import com.google.inject.AbstractModule;

public class ParserModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Parser.class).to(LatexParserImpl.class);
		bind(TreeParser.class).to(TreeParserImpl.class);
		bind(AnalyzersProvider.class).to(AnalyzersProviderImpl.class);
		bind(StringSimilarityEvaluator.class).to(
				StringSimilarityEvaluatorImpl.class);
		bind(Matcher.class).to(NameMatcher.class);
		bind(NameMatcherPropertiesLoader.class).to(
				NameMatcherPropertiesLoaderImpl.class);
		bind(ImportantNodeService.class).to(ImportantNodeServiceImpl.class);
	}

}
