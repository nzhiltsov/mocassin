/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.GateBasedReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceStatementExporter;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.ReferenceStatementExporterImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.GraphTopologyAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.HasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.NavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.ProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.BasicExemplifiesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.GraphTopologyAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.BasicHasConsequenceRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.BasicProvesRelationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.impl.WekaBasedNavigationalRelationClassifier;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * The class configures facilities to analyze the document structure
 * 
 * It uses the <strong>analyzer-module.properties</strong> file from the classpath,
 * which shouldn't be edited in general.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class DocumentAnalyzerModule extends AbstractModule {
    private Properties properties;

    /**
     * {@inheritDoc}
     */
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

	bind(HasConsequenceRelationAnalyzer.class).to(
		BasicHasConsequenceRelationAnalyzer.class);
	bind(ExemplifiesRelationAnalyzer.class).to(
		BasicExemplifiesRelationAnalyzer.class);
	bind(ProvesRelationAnalyzer.class)
		.to(BasicProvesRelationAnalyzer.class);

	bind(NavigationalRelationClassifier.class).to(
		WekaBasedNavigationalRelationClassifier.class);
	bind(ReferenceSearcher.class).to(GateBasedReferenceSearcher.class);
	bind(ReferenceStatementExporter.class).to(
		ReferenceStatementExporterImpl.class);
	bind(GraphTopologyAnalyzer.class).to(GraphTopologyAnalyzerImpl.class);
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
