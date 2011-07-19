package ru.ksu.niimm.cll.mocassin.nlp;

import gate.creole.SerialAnalyserController;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.nlp.gate.AnnieControllerProvider;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateStructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.impl.AnnieControllerProviderImpl;
import ru.ksu.niimm.cll.mocassin.nlp.gate.impl.GateDocumentDAOImpl;
import ru.ksu.niimm.cll.mocassin.nlp.gate.impl.GateProcessingFacadeImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.AnnotationAnalyzerImpl;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexStructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.latex.impl.LatexStructuralElementSearcherImpl;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.impl.StructuralElementTypeRecognizerImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.NlpModulePropertiesLoader;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.NlpModulePropertiesLoaderImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.ReferenceTripleUtilImpl;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.StopWordLoaderImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

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
		bind(NlpModulePropertiesLoader.class).to(
				NlpModulePropertiesLoaderImpl.class);
		bind(AnnotationAnalyzer.class).to(AnnotationAnalyzerImpl.class);
		bind(StructuralElementSearcher.class).to(
				GateStructuralElementSearcher.class);
		bind(LatexStructuralElementSearcher.class).to(
				LatexStructuralElementSearcherImpl.class);
		bind(ReferenceSearcher.class).to(GateReferenceSearcher.class);
		bind(AnnotationUtil.class).to(AnnotationUtilImpl.class);
		bind(StopWordLoader.class).to(StopWordLoaderImpl.class);
		bind(GateDocumentDAO.class).to(GateDocumentDAOImpl.class);
		bind(StructuralElementTypeRecognizer.class).to(
				StructuralElementTypeRecognizerImpl.class);
		bind(ReferenceTripleUtil.class).to(ReferenceTripleUtilImpl.class);
		bind(GateProcessingFacade.class).to(GateProcessingFacadeImpl.class);
		ThrowingProviderBinder
				.create(binder())
				.bind(AnnieControllerProvider.class,
						SerialAnalyserController.class)
				.to(AnnieControllerProviderImpl.class).in(Singleton.class);

	}

}
