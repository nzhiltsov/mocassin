package ru.ksu.niimm.cll.mocassin.analyzer;

import ru.ksu.niimm.cll.mocassin.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.analyzer.importance.impl.ImportantNodeServiceImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.location.impl.ReferenceElementLocationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LSIPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl.LSIPropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl.LatentSemanticIndexerImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl.NameMatcher;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl.NameMatcherPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.analyzer.mapping.matchers.impl.NameMatcherPropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.pos.VerbBasedFeatureAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.pos.impl.VerbBasedFeatureAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.impl.HasConsequenceRelationAnalyzerImpl;

import com.google.inject.AbstractModule;

public class AnalyzerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Matcher.class).to(NameMatcher.class);
		bind(NameMatcherPropertiesLoader.class).to(
				NameMatcherPropertiesLoaderImpl.class);
		bind(ImportantNodeService.class).to(ImportantNodeServiceImpl.class);
		bind(LatentSemanticIndexer.class).to(LatentSemanticIndexerImpl.class);
		bind(LSIPropertiesLoader.class).to(LSIPropertiesLoaderImpl.class);
		
		bind(ReferenceElementLocationAnalyzer.class).to(
				ReferenceElementLocationAnalyzerImpl.class);
		bind(VerbBasedFeatureAnalyzer.class).to(
				VerbBasedFeatureAnalyzerImpl.class);
		bind(HasConsequenceRelationAnalyzer.class).to(
				HasConsequenceRelationAnalyzerImpl.class);
	}

}
