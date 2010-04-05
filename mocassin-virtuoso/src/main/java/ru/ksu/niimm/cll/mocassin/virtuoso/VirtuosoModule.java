package ru.ksu.niimm.cll.mocassin.virtuoso;

import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.impl.InsertQueryGeneratorImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.VirtuosoDAOImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.validation.ValidateGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.validation.ValidateGraphInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class VirtuosoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(VirtuosoDAO.class).to(VirtuosoDAOImpl.class);
		bind(InsertQueryGenerator.class).to(InsertQueryGeneratorImpl.class);

		ValidateGraphInterceptor validateGraphInterceptor = new ValidateGraphInterceptor();
		bindInterceptor(Matchers.any(), Matchers
				.annotatedWith(ValidateGraph.class), validateGraphInterceptor);
	}

}
