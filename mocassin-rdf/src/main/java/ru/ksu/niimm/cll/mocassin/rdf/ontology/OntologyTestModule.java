package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import org.openrdf.repository.Repository;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.impl.InMemoryRepositoryProvider;

import com.google.inject.Singleton;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

public class OntologyTestModule extends OntologyModule {

	@Override
	protected void configureStore() {
		ThrowingProviderBinder.create(binder())
				.bind(RepositoryProvider.class, Repository.class)
				.to(InMemoryRepositoryProvider.class).in(Singleton.class);
	}

}
