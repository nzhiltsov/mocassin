package ru.ksu.niimm.cll.mocassin.ontology;

import org.openrdf.repository.Repository;

import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.ontology.provider.impl.InMemoryRepositoryProvider;

import com.google.inject.throwingproviders.ThrowingProviderBinder;


public class OntologyTestModule extends OntologyModule {

	@Override
	protected void configureStore() {
		ThrowingProviderBinder.create(binder())
		.bind(RepositoryProvider.class, Repository.class)
		.to(InMemoryRepositoryProvider.class);
	}

}
