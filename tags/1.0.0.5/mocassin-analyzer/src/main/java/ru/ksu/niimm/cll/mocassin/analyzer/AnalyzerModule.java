package ru.ksu.niimm.cll.mocassin.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.ksu.niimm.cll.mocassin.analyzer.classifier.NavRelClassifierImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.classifier.NavigationalRelationClassifier;
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
import ru.ksu.niimm.cll.mocassin.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.impl.ExemplifiesRelationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.impl.HasConsequenceRelationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.relation.impl.ProvesRelationAnalyzerImpl;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

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
		bind(ExemplifiesRelationAnalyzer.class).to(
				ExemplifiesRelationAnalyzerImpl.class);
		bind(ProvesRelationAnalyzer.class).to(ProvesRelationAnalyzerImpl.class);

		bind(NavigationalRelationClassifier.class).to(
				NavRelClassifierImpl.class);

		bindClassifier();

		bindTrainingSetHeader();
	}

	private void bindTrainingSetHeader() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(this
				.getClass().getClassLoader().getResourceAsStream(
						"mocassin-nav-relation-training-set.arff")));
		try {
			Instances data = new Instances(reader);
			reader.close();
			data.setClassIndex(data.numAttributes() - 1);
			bind(Instances.class).annotatedWith(
					Names.named("training.set.header")).toInstance(data);
		} catch (IOException e) {
			throw new RuntimeException(
					"couldn't read the navigation relations training set header");
		}
	}

	private void bindClassifier() {
		Classifier classifier;
		try {
			classifier = (Classifier) SerializationHelper.read(this.getClass()
					.getClassLoader().getResourceAsStream(
							"mocassin-nav-relations-j48.model"));
		} catch (Exception e) {
			throw new RuntimeException(
					"couldn't read the navigation relations learning model");
		}

		bind(Classifier.class).annotatedWith(Names.named("classifier"))
				.toInstance(classifier);
	}
}