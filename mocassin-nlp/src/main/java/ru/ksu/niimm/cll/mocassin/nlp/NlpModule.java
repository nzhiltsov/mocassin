package ru.ksu.niimm.cll.mocassin.nlp;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.StopWordLoaderImpl;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class NlpModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("nlp_module.properties"));
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
	}

}
