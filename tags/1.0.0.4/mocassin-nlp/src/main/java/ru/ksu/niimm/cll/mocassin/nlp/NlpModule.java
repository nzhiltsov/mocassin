package ru.ksu.niimm.cll.mocassin.nlp;

import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.impl.GateDocumentDAOImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.AnnotationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.FeatureExtractorImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceSearcherImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementSearcherImpl;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.impl.StructuralElementTypeRecognizerImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.NlpModulePropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.StopWordLoaderImpl;

import com.google.inject.AbstractModule;

public class NlpModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FeatureExtractor.class).to(FeatureExtractorImpl.class);
		bind(NlpModulePropertiesLoader.class).to(
				NlpModulePropertiesLoaderImpl.class);
		bind(AnnotationAnalyzer.class).to(AnnotationAnalyzerImpl.class);
		bind(StructuralElementSearcher.class).to(
				StructuralElementSearcherImpl.class);
		bind(ReferenceSearcher.class).to(ReferenceSearcherImpl.class);
		bind(AnnotationUtil.class).to(AnnotationUtilImpl.class);
		bind(StopWordLoader.class).to(StopWordLoaderImpl.class);
		bind(GateDocumentDAO.class).to(GateDocumentDAOImpl.class);
		bind(StructuralElementTypeRecognizer.class).to(
				StructuralElementTypeRecognizerImpl.class);
	}

}
