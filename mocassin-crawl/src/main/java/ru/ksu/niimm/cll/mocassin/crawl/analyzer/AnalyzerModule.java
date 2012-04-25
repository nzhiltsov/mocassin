package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.classifier.NavRelClassifierImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.classifier.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.GateReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.ReferenceStatementGeneratorImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.ImportantElementService;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.ImportantNodeService;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.impl.PageRankElementService;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.importance.impl.PageRankNodeService;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.impl.ReferenceElementLocationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.LSIPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl.LSIPropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl.LatentSemanticIndexerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.matchers.Matcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.matchers.impl.NameMatcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.matchers.impl.NameMatcherPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.mapping.matchers.impl.NameMatcherPropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.pos.VerbBasedFeatureAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.pos.impl.VerbBasedFeatureAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.ExemplifiesRelationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.HasConsequenceRelationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.ProvesRelationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class AnalyzerModule extends AbstractModule {
	private Properties properties;

	@Override
	protected void configure() {
		try {
			this.properties = new Properties();
			this.properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("analyzer-module.properties"));
			Names.bindProperties(binder(), this.properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the Analyzer module configuration");
		}
		bind(Matcher.class).to(NameMatcher.class);
		bind(NameMatcherPropertiesLoader.class).to(
				NameMatcherPropertiesLoaderImpl.class);

		bind(ImportantNodeService.class).to(PageRankNodeService.class);
		bind(ImportantElementService.class).to(PageRankElementService.class);

		bind(LatentSemanticIndexer.class).to(LatentSemanticIndexerImpl.class);
		bind(LSIPropertiesLoader.class).to(LSIPropertiesLoaderImpl.class);

		bind(ReferenceElementLocationAnalyzer.class).to(
				ReferenceElementLocationAnalyzerImpl.class);
		bind(VerbBasedFeatureAnalyzer.class).to(
				VerbBasedFeatureAnalyzerImpl.class);
		bind(HasConsequenceRelationAnalyzer.class).to(
				HasConsequenceRelationAnalyzerImpl.class);
		bind(ExemplifiesRelationAnalyzer.class).to(
				ExemplifiesRelationAnalyzerImpl.class);
		bind(ProvesRelationAnalyzer.class).to(ProvesRelationAnalyzerImpl.class);

		bind(NavigationalRelationClassifier.class).to(
				NavRelClassifierImpl.class);
		bind(ReferenceSearcher.class).to(GateReferenceSearcher.class);
		bind(ReferenceStatementGenerator.class).to(
				ReferenceStatementGeneratorImpl.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
		bindClassifier();

		bindTrainingSetHeader();
	}

	private void bindTrainingSetHeader() {
		BufferedReader reader = getHeaderReader();
		try {
			Instances data = new Instances(reader);
			reader.close();
			data.setClassIndex(data.numAttributes() - 1);
			bind(Instances.class).annotatedWith(
					Names.named("training.set.header")).toInstance(data);
		} catch (IOException e) {
			throw new RuntimeException(
					"Failed to read the navigation relations training set header");
		}
	}

	private BufferedReader getHeaderReader() {
		return new BufferedReader(new InputStreamReader(this
				.getClass()
				.getClassLoader()
				.getResourceAsStream(
						this.properties
								.getProperty("training.set.header.filename"))));
	}

	private void bindClassifier() {
		Classifier classifier;
		try {
			classifier = (Classifier) SerializationHelper
					.read(this
							.getClass()
							.getClassLoader()
							.getResourceAsStream(
									this.properties
											.getProperty("persisted.learning.model.filename")));
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to read the learning model for navigational relations");
		}

		bind(Classifier.class).annotatedWith(Names.named("classifier"))
				.toInstance(classifier);
	}
}
