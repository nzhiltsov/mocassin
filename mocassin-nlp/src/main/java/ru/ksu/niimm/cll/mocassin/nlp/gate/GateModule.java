package ru.ksu.niimm.cll.mocassin.nlp.gate;

import gate.creole.SerialAnalyserController;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.nlp.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.nlp.util.impl.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

public class GateModule extends AbstractModule {

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
		bind(GateDocumentDAO.class).to(GateDocumentDAOImpl.class);
		bind(GateProcessingFacade.class).to(GateProcessingFacadeImpl.class);
		bind(AnnotationUtil.class).to(AnnotationUtilImpl.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
		ThrowingProviderBinder
				.create(binder())
				.bind(AnnieControllerProvider.class,
						SerialAnalyserController.class)
				.to(AnnieControllerProviderImpl.class);
	}

}
