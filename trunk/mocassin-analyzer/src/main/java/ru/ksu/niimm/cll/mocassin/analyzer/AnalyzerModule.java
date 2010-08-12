package ru.ksu.niimm.cll.mocassin.analyzer;


import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.analyzer.importance.impl.ImportantNodeServiceImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl.NameMatcher;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl.NameMatcherPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl.NameMatcherPropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.StringSimilarityEvaluator;
import ru.ksu.niimm.cll.mocassin.analyzer.similarity.impl.StringSimilarityEvaluatorImpl;



import com.google.inject.AbstractModule;

public class AnalyzerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(StringSimilarityEvaluator.class).to(
				StringSimilarityEvaluatorImpl.class);
		bind(Matcher.class).to(NameMatcher.class);
		bind(NameMatcherPropertiesLoader.class).to(
				NameMatcherPropertiesLoaderImpl.class);
		bind(ImportantNodeService.class).to(ImportantNodeServiceImpl.class);
	}

}
