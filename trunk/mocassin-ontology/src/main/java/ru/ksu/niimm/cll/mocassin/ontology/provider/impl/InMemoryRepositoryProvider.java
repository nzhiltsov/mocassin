package ru.ksu.niimm.cll.mocassin.ontology.provider.impl;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.google.inject.Singleton;

import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;

@Singleton
public class InMemoryRepositoryProvider implements
		RepositoryProvider<Repository> {

	@Override
	public Repository get() throws RepositoryException {
		Repository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		return repository;
	}

}
