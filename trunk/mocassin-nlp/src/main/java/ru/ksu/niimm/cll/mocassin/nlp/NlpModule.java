package ru.ksu.niimm.cll.mocassin.nlp;

import ru.ksu.niimm.cll.mocassin.nlp.impl.FeatureExtractorImpl;

import com.google.inject.AbstractModule;

public class NlpModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FeatureExtractor.class).to(FeatureExtractorImpl.class);

	}

}
