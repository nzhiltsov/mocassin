package ru.ksu.niimm.cll.mocassin.ontology.provider.impl;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
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
		RepositoryConnection connection = repository.getConnection();
		try {
			connection.add(
					getClass().getResourceAsStream("/bootstrapdata.rdf"),
					"http://cll.niimm.ksu.ru/mocassinfortest", RDFFormat.N3);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			connection.close();
		}
		return repository;
	}

}
