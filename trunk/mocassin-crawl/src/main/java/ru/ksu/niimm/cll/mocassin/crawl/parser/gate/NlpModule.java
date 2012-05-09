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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.StopWordLoaderImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class NlpModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("nlp-module.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the NLP module configuration");
		}
		bind(StructuralElementSearcher.class).to(
				GateStructuralElementSearcher.class);
		bind(AnnotationUtil.class).to(AnnotationUtilImpl.class);
		bind(StopWordLoader.class).to(StopWordLoaderImpl.class);
		bind(StructuralElementTypeRecognizer.class).to(
				StructuralElementTypeRecognizerImpl.class);
		bind(CitationSearcher.class).to(GateCitationSearcher.class);
		bind(BibliographyExtractor.class).to(FakeBibliographyExtractor.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}

}
